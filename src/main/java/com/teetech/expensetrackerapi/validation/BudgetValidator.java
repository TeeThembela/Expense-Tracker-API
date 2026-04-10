package com.teetech.expensetrackerapi.validation;

import com.teetech.expensetrackerapi.dto.BudgetRequestDTO;
import com.teetech.expensetrackerapi.dto.BudgetUpdateDTO;
import com.teetech.expensetrackerapi.enums.BudgetPeriod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BudgetValidator {

    private static final BigDecimal maxBudgetAmount =  new BigDecimal("10000000");
    private static final BigDecimal minBudgetAmount =  new BigDecimal("0.01");

    //Validate budget creation request
    public List<String> validateBudgetCreationRequest(BudgetRequestDTO dto){
        log.debug("Validating budget creation request: amount={}, period={}, startDate={}, endDate={}",
                dto.amount(), dto.period(), dto.startDate(), dto.endDate());

        List<String> errors = validateBudget(dto.amount(), dto.period(), dto.startDate(), dto.endDate());

        if (!errors.isEmpty()) {
            log.warn("Budget creation validation failed with {} error(s): {}", errors.size(), errors);
        } else {
            log.debug("Budget creation validation passed");
        }

        return new ArrayList<>(errors);
    }

    // Validate budget update request
    public List<String> validateBudgetUpdate(BudgetUpdateDTO dto){
        log.debug("Validating budget update request: userId={}, categoryId={}, amount={}",
                dto.userId(), dto.categoryId(), dto.amount());

        List<String> errors = new ArrayList<>();

        //Check if at least one field is being updated
        if (dto.amount() == null && dto.period() == null && dto.startDate() == null
                && dto.endDate() == null && dto.categoryId() == null) {
            errors.add("At least one field must be provided for update");
            log.warn("Budget update validation failed: No fields provided for update");
            return errors;
        }

        errors.addAll(validateBudget(dto.amount(), dto.period(), dto.startDate(), dto.endDate()));

        if (!errors.isEmpty()) {
            log.warn("Budget update validation failed with {} error(s): {}", errors.size(), errors);
        } else {
            log.debug("Budget update validation passed");
        }

        return errors;
    }

    private List<String> validateBudget(BigDecimal amount, BudgetPeriod period, LocalDate startDate,
                                        LocalDate endDate){
        List<String> errors = new ArrayList<>();

        // Validate amount reasonableness
        if (amount != null){
            String amountError = isAmountReasonable(amount);
            if (amountError != null) {
                errors.add(amountError);
                log.debug("Budget amount validation failed: {}", amountError);
            }
        }

        // Validate period date alignment
        if (period != null || startDate != null || endDate != null){
            String periodError = validatePeriodDateAlignment(period, startDate, endDate);
            if (periodError != null) {
                errors.add(periodError);
                log.debug("Period-date alignment validation failed: {}", periodError);
            }
        }

        // Validate future start date warning
        if (startDate != null){
            LocalDate yearLater = LocalDate.now().plusYears(1);
            if (startDate.isAfter(yearLater)) {
                String warning = "Start date is more than 1 year in the future. Please verify this is correct.";
                errors.add(warning);
                log.info("Budget start date warning: startDate={} is more than 1 year in future", startDate);
            }
        }

        return errors;
    }

    // Validate that date range aligns with budget period
    public String validatePeriodDateAlignment(BudgetPeriod period, LocalDate startDate, LocalDate endDate){

        if (endDate == null ) {
            if (period == null || period == BudgetPeriod.CUSTOM) return null;
            return "Period should be CUSTOM or null since endDate is not provided";
        }

        if (period != null && period != BudgetPeriod.CUSTOM && startDate != null){
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            log.debug("Validating period alignment: period={}, days={}", period, days);

            return  switch (period) {
                case WEEKLY -> {
                    if (days < 6 || days > 7) {
                        yield "Weekly budget should span 6-7 days. Current span: " + days + " days";
                    }
                    yield null;
                }
                case MONTHLY -> {
                    if (days < 28 || days > 31) {
                        yield "Monthly budget should span 28-31 days. Current span: " + days + " days";
                    }
                    yield null;
                }
                case QUARTERLY -> {
                    if (days < 89 || days > 92) {
                        yield "Quarterly budget should span approximately 90 days (89-92). Current span: " + days + " days";
                    }
                    yield null;
                }
                case YEARLY -> {
                    if (days < 365 || days > 366) {
                        yield "Yearly budget should span 365-366 days. Current span: " + days + " days";
                    }
                    yield null;
                }
                default -> null;
            };
        }
        return null;
    }

    // Validate amount reasonableness
    public String isAmountReasonable(BigDecimal amount){
        if (amount.compareTo(minBudgetAmount) < 0 || amount.compareTo(maxBudgetAmount) > 0){
            return "Budget amount must be between " + minBudgetAmount + " and " + maxBudgetAmount;
        }
        return null;
    }
}