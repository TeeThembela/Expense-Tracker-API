package com.teetech.expensetrackerapi.service.impl;

import com.teetech.expensetrackerapi.dto.ExpenseFilterCriteria;
import com.teetech.expensetrackerapi.dto.ExpenseRequestDTO;
import com.teetech.expensetrackerapi.dto.ExpenseResponseDTO;
import com.teetech.expensetrackerapi.dto.ExpenseUpdateDTO;
import com.teetech.expensetrackerapi.entity.Budget;
import com.teetech.expensetrackerapi.entity.Category;
import com.teetech.expensetrackerapi.entity.Expense;
import com.teetech.expensetrackerapi.entity.User;
import com.teetech.expensetrackerapi.exception.BudgetNotFoundException;
import com.teetech.expensetrackerapi.exception.ExpenseNotFoundException;
import com.teetech.expensetrackerapi.exception.ValidationException;
import com.teetech.expensetrackerapi.mapper.ExpenseMapper;
import com.teetech.expensetrackerapi.repository.BudgetRepository;
import com.teetech.expensetrackerapi.repository.ExpenseRepository;
import com.teetech.expensetrackerapi.service.BudgetService;
import com.teetech.expensetrackerapi.service.CategoryService;
import com.teetech.expensetrackerapi.service.ExpenseService;
import com.teetech.expensetrackerapi.service.UserService;
import com.teetech.expensetrackerapi.validation.ExpenseValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseValidator validator;
    private final ExpenseRepository repository;
    private final ExpenseMapper mapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final BudgetRepository budgetRepository;

    // Create an expense for specific user
    @Transactional
    @Override
    public ExpenseResponseDTO createExpense(ExpenseRequestDTO dto, UUID userId) {
        log.info("Creating expense for userId: {}", userId);

        // Validate business rules before doing anything else
        List<String> validationErrors = validator.validateExpenseCreationRequest(dto);
        if (!validationErrors.isEmpty()){
            log.warn("Expense creation failed validation for userId: {} — {} error(s)",
                    userId, validationErrors.size());
            throw new ValidationException(validationErrors);
        }

        //Resolve relationships
        User user = userService.findUserOrThrow(userId);
        Category category = categoryService.findAccessibleCategory(dto.categoryId(), userId);


        // Map and wire relationship
        Expense expense = mapper.toExpense(dto);
        expense.setUser(user);
        expense.setCategory(category);

        // Persist entity
        Expense saved = repository.saveAndFlush(expense);
        log.info("Expense created successfully: expenseId={}, amount={}, category={}, userId={}",
                saved.getId(), saved.getAmount(), category.getName(), userId);

        // Map to DTO, then check budget — expense is already saved regardless of result
        ExpenseResponseDTO response = mapper.toExpenseDTO(saved);
        String warning = checkBudgetExceeded(userId, category, dto.expenseDate(), dto.amount(), null);

        if (warning != null) {
            response = withWarning(response, warning);
        }

        return response;
    }

    // Update an expense
    @Transactional
    @Override
    public ExpenseResponseDTO updateExpense(UUID expenseId, ExpenseUpdateDTO dto, UUID userId) {
        log.info("Updating expense: expenseId={}, userId={}", expenseId, userId);

        // Validate business rules
        List<String> validationErrors = validator.validateExpenseUpdate(dto);
        if (!validationErrors.isEmpty()){
            log.warn("Expense update failed validation: expenseId={} — {} error(s)", expenseId, validationErrors.size());
            throw new ValidationException(validationErrors);
        }

        // Fetch the expense and verifies ownership
        Expense expense = findExpenseOrThrow(expenseId, userId);

        // Resolve relationship
        Category category = expense.getCategory();
        if (dto.categoryId() != null){
            log.debug("Category change requested: newCategoryId={}", dto.categoryId());
            category = categoryService.findAccessibleCategory(dto.categoryId(), userId);
            expense.setCategory(category);
        }

        // Apply partial updates (null fields are ignored by MapStruct)
        mapper.toExpenseUpdate(dto, expense);

        // Persist
        Expense saved = repository.saveAndFlush(expense);
        log.info("Expense updated successfully: expenseId={}, userId={}", expenseId, userId);

        // Effective values after update — fall back to existing if not changed
        LocalDate effectiveDate = dto.expenseDate() != null ? dto.expenseDate() : saved.getExpenseDate();
        BigDecimal effectiveAmount = dto.amount() != null ? dto.amount() : saved.getAmount();

        ExpenseResponseDTO response = mapper.toExpenseDTO(saved);
        // Exclude this expense from the sum so it is not double-counted
        String warning = checkBudgetExceeded(userId, category, effectiveDate, effectiveAmount, expenseId);

        if (warning != null) {
            response = withWarning(response, warning);
        }

        return response;
    }

    // Get an expense
    @Transactional(readOnly = true)
    @Override
    public ExpenseResponseDTO getExpense(UUID expenseId, UUID userId) {
        log.debug("Fetching expense: expenseId: {}, userId: {}", expenseId, userId);

        // Fetch and verify ownership
        Expense expense = findExpenseOrThrow(expenseId, userId);
        return mapper.toExpenseDTO(expense);
    }

    // Get expenses
    @Transactional(readOnly = true)
    @Override
    public Page<ExpenseResponseDTO> getExpenses(UUID userId, Pageable pageable) {
        log.debug("Fetching expense: userId: {}, page: {}, size: {}", userId, pageable.getPageNumber(),
                    pageable.getPageSize());

        // Verify user exists
        userService.findUserOrThrow(userId);

        return repository.findByUserId(userId, pageable)
                .map(mapper::toExpenseDTO);
    }

    // Get expenses with filter
    @Transactional(readOnly = true)
    @Override
    public Page<ExpenseResponseDTO> getExpensesFiltered(UUID userId, ExpenseFilterCriteria filter, Pageable pageable) {
        log.debug("Fetching filtered expenses for userId: {}, period: {}, categoryId: {}, " +
                        "startDate: {}, endDate: {}",
                userId, filter.period(), filter.categoryId(), filter.startDate(), filter.endDate());

        // Verify user exists
        userService.findUserOrThrow(userId);

        // Resolve period string to concrete start/end dates
        LocalDate startDate = filter.startDate();
        LocalDate endDate = filter.endDate();

        if (filter.period() != null){
            //Validate the filter criteria
            validateFilterCriteria(filter.period(), startDate, endDate);

            LocalDate[] range = resolvePeriodDateRanges(filter.period(), startDate, endDate);
            startDate = range[0];
            endDate = range[1];
            log.debug("Period '{}' resolved to: startDate={}, endDate={}", filter.period(), startDate, endDate);
        }

        boolean hasDateRange = startDate != null && endDate != null;
        boolean hasCategoryFilter = filter.categoryId() != null;

        //Route to correct repository query
        if (hasDateRange && hasCategoryFilter){
            log.debug("Applying date range + category filter");
            return repository.findByUserIdAndCategoryIdAndExpenseDateBetween(userId, filter.categoryId(), startDate,
                    endDate, pageable)
                    .map(mapper::toExpenseDTO);
        } else if (hasCategoryFilter) {
            log.debug("Applying category filter");
            return repository.findByCategoryIdAndUserId(filter.categoryId(), userId, pageable)
                    .map(mapper::toExpenseDTO);
        }else {
            log.debug("Applying date range");
            return repository.findByUserIdAndExpenseDate(userId, startDate, endDate, pageable)
                    .map(mapper::toExpenseDTO);
        }

    }

    // Delete an expense
    @Transactional
    @Override
    public void deleteExpense(UUID expenseId, UUID userId) {
        log.info("Deleting expense: expenseId={}, userId={}", expenseId, userId);

        // Verify ownership before deleting
        Expense expense = findExpenseOrThrow(expenseId, userId);
        repository.delete(expense);

        log.info("Expense deleted successfully: expenseId={}, userId={}", expenseId, userId);
    }

    // Helper Methods
        /**
         * Find expense by expense id and user id
         * If not found throw ExpenseNotFoundException
         */
        @Transactional(readOnly = true)
        protected Expense findExpenseOrThrow(UUID expenseId, UUID userId){
            log.debug("Fetching expense by id: expenseId={}, userId={}", expenseId, userId);
            return repository.findByIdAndUserId(expenseId, userId)
                    .orElseThrow(() -> {
                        log.warn("Expense not found or access denied: expenseId={}, userId={}", expenseId, userId);
                        return new ExpenseNotFoundException(expenseId.toString());
                    });
    }

        /**
         * Converts a period string into a [startDate, endDate] array
         * CUSTOM period uses the already period in the filter
         */
        private LocalDate[] resolvePeriodDateRanges(String period, LocalDate customStart, LocalDate customEnd){
            LocalDate today = LocalDate.now();

            return switch (period.toUpperCase()){
                case "PAST_WEEK" -> new LocalDate[]{today.minusDays(7), today};
                case "PAST_MONTH" -> new LocalDate[]{today.minusDays(30), today};
                case "LAST_3_MONTHS" -> new LocalDate[]{today.minusDays(90), today};
                case "CUSTOM" -> new LocalDate[]{customStart, customEnd};
                default -> {
                    log.warn("Unknown period filter value: '{}' — no date range applied", period);
                    yield new LocalDate[]{null, null};
                }
            };
        }

        /**
         * Check if the period is CUSTOM, fields are provided correctly
         * Validate the startDate and endDate are reasonable
         */
        private void validateFilterCriteria(String period, LocalDate startDate, LocalDate endDate) {
            if (period.equalsIgnoreCase("CUSTOM") && (startDate == null || endDate == null)) {
                log.warn("CUSTOM period requires both startDate and endDate. startDate={}, endDate={}",
                        startDate, endDate);
                throw new IllegalArgumentException("Both startDate and endDate are required when period is CUSTOM.");
            }
        }

        /**
         * Reconstructs the ExpenseResponseDTO record with a budgetWarning set.
         * Records are immutable so a new instance must be created.
         */
        private ExpenseResponseDTO withWarning(ExpenseResponseDTO response, String warning) {
            return new ExpenseResponseDTO(
                    response.id(),
                    response.amount(),
                    response.expenseDate(),
                    response.categoryId(),
                    response.categoryName(),
                    response.description(),
                    response.createdAt(),
                    response.updatedAt(),
                    warning
            );
        }

    /**
     *Checks if adding a new expense to an existing budget exceeds the limit.
     * The process involves finding the applicable budget,
     * summing prior expenses in the same category, adding the new amount,
     * and returning a warning if the budget is exceeded.
     * The expense is always saved, serving as a soft warning rather than a restriction.
     */
        private String checkBudgetExceeded(UUID userId, Category category,
                                           LocalDate expenseDate, BigDecimal newAmount,
                                           UUID excludeExpenseId) {
            log.debug("Checking budget for userId={}, categoryId={}, expenseDate={}, amount={}",
                    userId, category.getId(), expenseDate, newAmount);

            // Find the applicable budget for this category on the expense date
            Optional<Budget> budgetOpt = budgetRepository.findBudgetForCategoryOnDate(
                    userId, category.getId(), expenseDate);

            if (budgetOpt.isEmpty()) {
                log.debug("No budget found for categoryId={} on date={} — skipping budget check",
                        category.getId(), expenseDate);
                return null;
            }

            Budget budget = budgetOpt.get();

            // Sum all existing expenses within this budget's date range,
            // Excluding the current expense on update to avoid double-counting
            LocalDate budgetStart = budget.getStartDate();
            LocalDate budgetEnd = budget.getEndDate() != null ? budget.getEndDate() : LocalDate.now();

            BigDecimal alreadySpent = repository.sumAmountByUserAndCategoryAndDateRange(
                    userId, category.getId(), budgetStart, budgetEnd, excludeExpenseId);

            // Add the new amount and check against the budget
            BigDecimal totalAfterExpense = alreadySpent.add(newAmount);

            log.debug("Budget check: budgetAmount={}, alreadySpent={}, newAmount={}, totalAfter={}",
                    budget.getAmount(), alreadySpent, newAmount, totalAfterExpense);

            if (totalAfterExpense.compareTo(budget.getAmount()) > 0) {
                BigDecimal overspend = totalAfterExpense.subtract(budget.getAmount());
                String warning = String.format(
                        "Warning: This expense exceeds your '%s' budget by R%.2f. " +
                                "Budget: R%.2f | Total spent: R%.2f.",
                        category.getName(),
                        overspend,
                        budget.getAmount(),
                        totalAfterExpense
                );
                log.info("Budget exceeded: userId={}, category={}, overspend={}",
                        userId, category.getName(), overspend);
                return warning;
            }

            return null;
        }
}
