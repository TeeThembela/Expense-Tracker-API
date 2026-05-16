package com.teetech.expensetrackerapi.dto;

import com.teetech.expensetrackerapi.enums.BudgetPeriod;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(name = "BudgetResponse", description = "Full details of a budget record")
public record BudgetResponseDTO(

        @Schema(description = "Unique identifier of the budget", example = "1b6d2f3a-45e1-4c78-9ab0-123456789abc")
        UUID id,

        @Schema(description = "UUID of the assigned category", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
        UUID categoryId,

        @Schema(description = "Display name of the assigned category", example = "Groceries")
        String categoryName,

        @Schema(description = "Maximum spending amount for the period", example = "3000.00")
        BigDecimal amount,

        @Schema(description = "Recurrence period, or null for custom date-range budgets",
                example = "MONTHLY", nullable = true)
        BudgetPeriod period,

        @Schema(description = "Start date of the budget window (ISO 8601: yyyy-MM-dd)", example = "2025-05-01")
        LocalDate startDate,

        @Schema(description = "End date of the budget window (ISO 8601: yyyy-MM-dd), or null for open-ended budgets",
                example = "2025-05-31", nullable = true)
        LocalDate endDate,

        @Schema(description = "UTC timestamp when the budget was created", example = "2025-05-01T07:00:00")
        LocalDateTime createdAt,

        @Schema(description = "UTC timestamp of the last update", example = "2025-05-01T07:00:00")
        LocalDateTime updatedAt

) {}