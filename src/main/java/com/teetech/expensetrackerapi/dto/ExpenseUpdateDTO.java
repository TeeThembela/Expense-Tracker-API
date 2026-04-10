package com.teetech.expensetrackerapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseUpdateDTO(

        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @PastOrPresent(message = "Expense date must be in the present or past")
        LocalDate expenseDate,

        UUID categoryId,

        String description
) {
}
