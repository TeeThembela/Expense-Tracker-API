package com.teetech.expensetrackerapi.validation;

import com.teetech.expensetrackerapi.dto.ExpenseRequestDTO;
import com.teetech.expensetrackerapi.dto.ExpenseUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ExpenseValidator {

    private static final BigDecimal MIN_EXPENSE_AMOUNT = new BigDecimal("0.01");
    private static final BigDecimal MAX_EXPENSE_AMOUNT = new BigDecimal("1000000");
    private static final int MAX_PAST_DAYS = 365;

    // Validate expense creation request
    public List<String> validateExpenseCreationRequest(ExpenseRequestDTO dto){
        log.debug("Validating expense creation request: amount={}, expenseDate={}, categoryId={}",
                dto.amount(), dto.expenseDate(), dto.categoryId());

        List<String> errors = validateExpenseRequest(dto.amount(), dto.expenseDate(), dto.description());

        if (!errors.isEmpty()) {
            log.warn("Expense creation validation failed with {} error(s): {}", errors.size(), errors);
        } else {
            log.debug("Expense creation validation passed");
        }

        return new ArrayList<>(errors);
    }

    // Validate expense update request
    public List<String> validateExpenseUpdate(ExpenseUpdateDTO dto){
        log.debug("Validating expense update request: amount={}, expenseDate={}, categoryId={}",
                dto.amount(), dto.expenseDate(), dto.categoryId());

        List<String> errors = new ArrayList<>();

        // Check if at least one field is being updated
        if (dto.amount() == null && dto.expenseDate() == null && dto.categoryId() == null
                && dto.description()== null) {
            errors.add("At least one field must be provided for update");
            log.warn("Expense update validation failed: No fields provided for update");
            return errors;
        }

        errors.addAll(validateExpenseRequest(dto.amount(), dto.expenseDate(), dto.description()));

        if (!errors.isEmpty()) {
            log.warn("Expense update validation failed with {} error(s): {}", errors.size(), errors);
        } else {
            log.debug("Expense update validation passed");
        }

        return errors;
    }

    private List<String> validateExpenseRequest(BigDecimal amount, LocalDate expenseDate, String description){
        List<String> errors = new ArrayList<>();

        // Validate amount reasonableness
        if (amount != null){
            String amountError = isAmountReasonable(amount);
            if (amountError != null) {
                errors.add(amountError);
                log.debug("Expense amount validation failed: amount={}, error={}", amount, amountError);
            }
        }

        // Validate expense date warning
        if (expenseDate != null){
            // Warn if expense id very old
            if (ValidationHelper.isOlderThan(expenseDate, MAX_PAST_DAYS)){
                String warning = "Warning: Expense date is more than 1 year in the past. Please verify this is correct.";
                errors.add(warning);
                log.info("Old expense date warning: expenseDate={}", expenseDate);
            }

            // Prevent future dates beyond tomorrow
            if (ValidationHelper.isFurtherThan(expenseDate, 1)){
                errors.add("Expense date cannot be more than 1 day in the future");
                log.warn("Future expense date rejected: expenseDate={}", expenseDate);
            }
        }

        // Validate description if provided
        if (description != null){
            if (description.length() > 500){
                errors.add("Description cannot exceed 500 characters");
                log.debug("Description too long: length={}", description.length());
            }
        }

        return errors;
    }

    public String isAmountReasonable(BigDecimal amount){
        if (amount.compareTo(MIN_EXPENSE_AMOUNT) < 0 || amount.compareTo(MAX_EXPENSE_AMOUNT) > 0){
            return "Expense amount must be between " + MIN_EXPENSE_AMOUNT + " and " + MAX_EXPENSE_AMOUNT;
        }
        return null;
    }
}