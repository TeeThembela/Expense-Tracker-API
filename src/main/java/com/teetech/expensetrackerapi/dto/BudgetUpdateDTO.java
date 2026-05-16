package com.teetech.expensetrackerapi.dto;

import com.teetech.expensetrackerapi.enums.BudgetPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "BudgetUpdate",
        description = "Partial update payload for a budget — all fields are optional; only supplied fields are changed")
public record BudgetUpdateDTO(

        @Schema(description = "UUID of the replacement category — must belong to the same user",
                example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
        UUID categoryId,

        @Schema(description = "New spending limit — must be greater than zero if supplied",
                example = "4000.00")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @Schema(description = "Updated recurrence period",
                example = "WEEKLY",
                nullable = true)
        BudgetPeriod period,

        @Schema(description = "New start date (ISO 8601: yyyy-MM-dd)",
                example = "2025-06-01")
        LocalDate startDate,

        @Schema(description = "New end date (ISO 8601: yyyy-MM-dd) — must be on or after startDate if both are supplied",
                example = "2025-06-30")
        LocalDate endDate

) {
    public BudgetUpdateDTO {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }
    }
}