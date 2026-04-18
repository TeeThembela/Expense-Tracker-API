package com.teetech.expensetrackerapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ExpenseResponseDTO(
        UUID id,
        BigDecimal amount,
        LocalDate expenseDate,
        UUID categoryId,
        String categoryName,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String budgetWarning
) {
}
