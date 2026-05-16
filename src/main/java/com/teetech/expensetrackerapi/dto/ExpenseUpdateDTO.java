package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "ExpenseUpdate",
        description = "Partial update payload for an expense — all fields are optional; only supplied fields are changed")
public record ExpenseUpdateDTO(

        @Schema(description = "New amount — must be greater than zero if supplied",
                example = "200.00")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @Schema(description = "New expense date — cannot be a future date if supplied (ISO 8601: yyyy-MM-dd)",
                example = "2025-05-12")
        @PastOrPresent(message = "Expense date must be in the present or past")
        LocalDate expenseDate,

        @Schema(description = "UUID of the replacement category — must belong to the same user",
                example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
        UUID categoryId,

        @Schema(description = "Updated note — max 500 characters",
                example = "Top-up grocery run",
                maxLength = 500)
        @Size(max = 500)
        String description

) {}