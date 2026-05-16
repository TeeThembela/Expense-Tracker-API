package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "ExpenseRequest", description = "Payload required to record a new expense")
public record ExpenseRequestDTO(

        @Schema(description = "Amount spent — must be greater than zero",
                example = "149.99",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @Schema(description = "Date the expense occurred — cannot be a future date (ISO 8601: yyyy-MM-dd)",
                example = "2025-05-10",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Expense Date is required")
        @PastOrPresent(message = "Expense Date must be in the present or past")
        LocalDate expenseDate,

        @Schema(description = "UUID of the category to assign this expense to — must belong to the same user",
                example = "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Category is required")
        UUID categoryId,

        @Schema(description = "Optional note describing the expense — max 500 characters",
                example = "Monthly grocery run at Woolworths",
                maxLength = 500)
        @Size(max = 500)
        String description

) {}