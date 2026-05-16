package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(name = "ExpenseResponse", description = "Full details of a recorded expense")
public record ExpenseResponseDTO(

        @Schema(description = "Unique identifier of the expense", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
        UUID id,

        @Schema(description = "Amount spent", example = "149.99")
        BigDecimal amount,

        @Schema(description = "Date the expense occurred (ISO 8601: yyyy-MM-dd)", example = "2025-05-10")
        LocalDate expenseDate,

        @Schema(description = "UUID of the assigned category", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID categoryId,

        @Schema(description = "Display name of the assigned category", example = "Groceries")
        String categoryName,

        @Schema(description = "Optional note describing the expense", example = "Monthly grocery run at Woolworths")
        String description,

        @Schema(description = "UTC timestamp when the record was created", example = "2025-05-10T08:15:00")
        LocalDateTime createdAt,

        @Schema(description = "UTC timestamp of the last update", example = "2025-05-10T09:00:00")
        LocalDateTime updatedAt,

        @Schema(description = "Non-null warning message when this expense causes the category budget to be exceeded or approached. " +
                "Null when no relevant budget exists or the budget is within threshold.",
                example = "Warning: You have exceeded your Groceries budget for this period.",
                nullable = true)
        String budgetWarning

) {}
