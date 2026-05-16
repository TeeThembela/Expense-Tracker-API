package com.teetech.expensetrackerapi.dto;

import com.teetech.expensetrackerapi.enums.BudgetPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "BudgetRequest", description = "Payload required to create a new budget for a category")
public record BudgetRequestDTO(

        @Schema(description = "UUID of the category this budget applies to — must belong to the same user",
                example = "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Category is required")
        UUID categoryId,

        @Schema(description = "Maximum spending amount for the budget period — must be greater than zero",
                example = "3000.00",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @Schema(description = "Predefined recurrence period. Leave null when supplying a custom date range via startDate/endDate.",
                example = "MONTHLY",
                nullable = true)
        BudgetPeriod period,

        @Schema(description = "Start date of the budget window (ISO 8601: yyyy-MM-dd)",
                example = "2025-05-01",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @Schema(description = "End date of the budget window (ISO 8601: yyyy-MM-dd). " +
                "Must be on or after startDate. Leave null for open-ended or period-based budgets.",
                example = "2025-05-31",
                nullable = true)
        LocalDate endDate

) {
    public BudgetRequestDTO {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }
    }
}
