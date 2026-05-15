package com.teetech.expensetrackerapi.service.impl;

import com.teetech.expensetrackerapi.dto.BudgetRequestDTO;
import com.teetech.expensetrackerapi.dto.BudgetResponseDTO;
import com.teetech.expensetrackerapi.dto.BudgetUpdateDTO;
import com.teetech.expensetrackerapi.entity.Budget;
import com.teetech.expensetrackerapi.entity.Category;
import com.teetech.expensetrackerapi.entity.User;
import com.teetech.expensetrackerapi.exception.BudgetNotFoundException;
import com.teetech.expensetrackerapi.exception.DuplicateBudgetException;
import com.teetech.expensetrackerapi.exception.ValidationException;
import com.teetech.expensetrackerapi.mapper.BudgetMapper;
import com.teetech.expensetrackerapi.repository.BudgetRepository;
import com.teetech.expensetrackerapi.service.BudgetService;
import com.teetech.expensetrackerapi.service.CategoryService;
import com.teetech.expensetrackerapi.service.UserService;
import com.teetech.expensetrackerapi.validation.BudgetValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class BudgetServiceImpl implements BudgetService {
    private final BudgetMapper mapper;
    private final BudgetValidator validator;
    private final BudgetRepository repository;
    private final UserService userService;
    private final CategoryService categoryService;

    // Create a budget
    @Transactional
    @Override
    public BudgetResponseDTO createBudget(BudgetRequestDTO dto, UUID userId) {
        log.info("Creating budget for userId: {}", userId);

        // Validate business rules
        List<String> validationErrors = validator.validateBudgetCreationRequest(dto);
        if (!validationErrors.isEmpty()){
            log.warn("Budget creation failed validation for userId: {} — {} error(s)",
                    userId, validationErrors.size());
            throw new ValidationException(validationErrors);
        }

        // Resolve relationships
        User user = userService.findUserOrThrow(userId);
        Category category = categoryService.findAccessibleCategory(dto.categoryId(), userId);

        // null = no budget to exclude on create
        checkBudgetOverlap(userId, category.getId(), dto.startDate(), dto.endDate(), null);

        // Map and wire relationship
        Budget budget = mapper.toBudget(dto);
        budget.setUser(user);
        budget.setCategory(category);

        // Persist entity
        Budget saved = repository.saveAndFlush(budget);
        log.info("Budget created successfully: budgetId={}, amount={}, category={}, userId={}",
                saved.getId(), saved.getAmount(), category.getName(), userId);

        // Map to DTO and return
        return mapper.toBudgetDTO(saved);
    }

    // Update a budget
    @Transactional
    @Override
    public BudgetResponseDTO updateBudget(UUID budgetId, BudgetUpdateDTO dto, UUID userId) {
        log.info("Updating budget: budgetId={}, userId={}", budgetId, userId);

        // Validate business rules
        List<String> validationErrors = validator.validateBudgetUpdate(dto);
        if (!validationErrors.isEmpty()){
            log.warn("Budget update failed validation: budgetId={} — {} error(s)", budgetId, validationErrors.size());
            throw new ValidationException(validationErrors);
        }

        // Fetch the budget and verifies ownership
        Budget budget = findBudgetOrThrow(budgetId, userId);

        // Resolve relationships
        Category category = budget.getCategory();
        if (dto.categoryId() != null){
            log.debug("Category change requested: newCategoryId: {}", dto.categoryId());
            category = categoryService.findAccessibleCategory(dto.categoryId(), userId);
            budget.setCategory(category);
        }

        // Use incoming value if provided, otherwise keep the existing value
        LocalDate effectiveStartDate = dto.startDate() != null ? dto.startDate() : budget.getStartDate();
        LocalDate effectiveEndDate = dto.endDate() != null ? dto.endDate() : budget.getEndDate();

        // Pass budgetId to exclude this budget from conflicting with itself
        checkBudgetOverlap(userId, category.getId(), effectiveStartDate, effectiveEndDate, budgetId);

        // Apply partial updates
        mapper.toBudgetUpdate(dto, budget);

        // Persist
        Budget saved = repository.saveAndFlush(budget);
        log.info("Budget updated successfully: budgetId={}, userId={}", budgetId, userId);

        // Map and return
        return mapper.toBudgetDTO(saved);
    }

    // Get a budget
    @Transactional(readOnly = true)
    @Override
    public BudgetResponseDTO getBudget(UUID budgetId, UUID userId) {
        log.debug("Fetching budget: budgetId: {}, userId: {}", budgetId, userId);

        // Fetch and verify ownership
        Budget budget = findBudgetOrThrow(budgetId, userId);
        return mapper.toBudgetDTO(budget);
    }

    // Get budgets
    @Transactional(readOnly = true)
    @Override
    public Page<BudgetResponseDTO> getBudgets(UUID userId, Pageable pageable) {
        log.debug("Fetching budgets: userId: {}, page: {}, size: {}", userId, pageable.getPageNumber(),
                pageable.getPageSize());

        // Verify user exists
        userService.findUserOrThrow(userId);

        // Map and return
        return repository.findAllByUserId(userId, pageable)
                .map(mapper::toBudgetDTO);
    }

    // Delete an budget
    @Transactional
    @Override
    public void deleteBudget(UUID budgetId, UUID userId) {
        log.info("Deleting budget: budgetId={}, userId={}", budgetId, userId);

        // Verify ownership before deleting
        Budget budget = findBudgetOrThrow(budgetId, userId);
        repository.delete(budget);

        log.info("Budget deleted successfully: budgetId={}, userId={}", budgetId, userId);
    }

    // Helper Methods
        /**
         * Find budget by budget id and user id
         * If not found throw BudgetNotFoundException
         */
        @Transactional(readOnly = true)
        protected Budget findBudgetOrThrow(UUID budgetId, UUID userId){
            log.debug("Fetching budget by id: budgetId={}, userId={}", budgetId, userId);

            return repository.findByIdAndUserId(budgetId, userId)
                    .orElseThrow(() -> {
                        log.warn("Budget not found or access denied: budgetId={}, userId={}", budgetId, userId);
                        return new BudgetNotFoundException(budgetId.toString());
                    });
        }

    /**
     * Checks whether the resolved date range overlaps with any existing budget
     * the user has for the same category.
     *
     * @param userId The user's ID
     * @param categoryId The category ID
     * @param newStartDate The resolved start date (must not be null)
     * @param newEndDate The resolved end date (can be null for open-ended budgets)
     * @param excludeBudgetId The ID of the budget to exclude (null for creation)
     */
    private void checkBudgetOverlap(UUID userId, UUID categoryId,
                                    LocalDate newStartDate, LocalDate newEndDate,
                                    UUID excludeBudgetId) {

        // Safety check: Never allow a null start date into the database check
        if (newStartDate == null) {
            throw new IllegalArgumentException("Start date cannot be null when checking overlaps");
        }

        log.debug("Checking budget overlap: userId={}, categoryId={}, startDate={}, endDate={}, excludeId={}",
                userId, categoryId, newStartDate, newEndDate, excludeBudgetId);

        boolean overlaps = excludeBudgetId == null
                ? repository.existsOverlappingBudget(userId, categoryId, newStartDate, newEndDate)
                : repository.existsOverlappingBudgetExcluding(userId, categoryId, newStartDate, newEndDate, excludeBudgetId);

        if (overlaps) {
            log.warn("Budget overlap detected: userId={}, categoryId={}, startDate={}, endDate={}",
                    userId, categoryId, newStartDate, newEndDate);
            throw new DuplicateBudgetException("A budget already exists for this category during the selected dates.");
        }
    }
}
