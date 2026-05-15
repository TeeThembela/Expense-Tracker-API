package com.teetech.expensetrackerapi.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseUpdateDTO(

        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @PastOrPresent(message = "Expense date must be in the present or past")
        LocalDate expenseDate,

        UUID categoryId,

        @Size(max = 500)
        String description
) {
}
