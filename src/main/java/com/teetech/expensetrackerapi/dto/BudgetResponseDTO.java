package com.teetech.expensetrackerapi.dto;

import com.teetech.expensetrackerapi.enums.BudgetPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BudgetResponseDTO(
        UUID id,
        UUID categoryId,
        String categoryName,
        BigDecimal amount,
        BudgetPeriod period,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
