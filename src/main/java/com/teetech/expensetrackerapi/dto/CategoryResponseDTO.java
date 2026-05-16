package com.teetech.expensetrackerapi.dto;

import com.teetech.expensetrackerapi.enums.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(name = "CategoryResponse", description = "Full details of an expense category")
public record CategoryResponseDTO(

        @Schema(description = "Unique identifier of the category", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
        UUID id,

        @Schema(description = "Category name", example = "Groceries")
        String name,

        @Schema(description = "Category description", example = "Weekly and monthly food and household supply purchases", nullable = true)
        String description,

        @Schema(description = "System-assigned category type (e.g. CUSTOM, SYSTEM)", example = "CUSTOM")
        CategoryType type,

        @Schema(description = "UTC timestamp when the category was created", example = "2025-04-01T10:00:00")
        LocalDateTime createdAt,

        @Schema(description = "UTC timestamp of the last update", example = "2025-04-01T10:00:00")
        LocalDateTime updatedAt

) {}
