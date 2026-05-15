package com.teetech.expensetrackerapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseRequestDTO (

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must greater than zero")
        BigDecimal amount,

        @NotNull(message = "Expense Date is required")
        @PastOrPresent(message = "Expense Date must in the Present or Past")
        LocalDate expenseDate,

        @NotNull(message = "Category is required")
        UUID categoryId,

        @Size(max = 500)
        String description
) {
}
