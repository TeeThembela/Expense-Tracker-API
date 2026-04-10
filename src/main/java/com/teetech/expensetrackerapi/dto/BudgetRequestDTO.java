package com.teetech.expensetrackerapi.dto;

import com.teetech.expensetrackerapi.enums.BudgetPeriod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BudgetRequestDTO(

        @NotNull(message = "Category is required")
        UUID categoryId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must greater than zero")
        BigDecimal amount,

        BudgetPeriod period,  // Can be null if using custom dates,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        LocalDate endDate
) {
    public BudgetRequestDTO{
        if (endDate != null && endDate.isBefore(startDate)){
            throw new IllegalArgumentException("End date must be on or after start date");
        }
    }
}
