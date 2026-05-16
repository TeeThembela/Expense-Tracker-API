package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(name = "CategoryUpdate",
        description = "Partial update payload for a category — all fields are optional; only supplied fields are changed")
public record CategoryUpdateDTO(

        @Schema(description = "New category name — max 100 characters",
                example = "Food & Groceries",
                maxLength = 100)
        @Size(max = 100, message = "Category name cannot exceed 100 characters")
        String name,

        @Schema(description = "Updated description — max 500 characters",
                example = "All food-related purchases including dining out",
                maxLength = 500)
        @Size(max = 500, message = "Category description cannot exceed 500 characters")
        String description

) {}