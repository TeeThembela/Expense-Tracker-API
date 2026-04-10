package com.teetech.expensetrackerapi.dto;

import com.teetech.expensetrackerapi.enums.BudgetPeriod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BudgetUpdateDTO(

        UUID categoryId,

        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        BudgetPeriod period,

        LocalDate startDate,

        LocalDate endDate
) {
    public BudgetUpdateDTO {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }
    }
}
