package com.teetech.expensetrackerapi.dto;

import com.teetech.expensetrackerapi.enums.CategoryType;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponseDTO(
        UUID id,
        String name,
        String description,
        CategoryType type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
