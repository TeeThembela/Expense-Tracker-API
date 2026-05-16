package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "CategoryRequest", description = "Payload required to create a new expense category")
public record CategoryRequestDTO(

        @Schema(description = "Name of the category — must be unique per user, max 100 characters",
                example = "Groceries",
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name cannot exceed 100 characters")
        String name,

        @Schema(description = "Optional description of what this category covers — max 500 characters",
                example = "Weekly and monthly food and household supply purchases",
                maxLength = 500)
        @Size(max = 500, message = "Category description cannot exceed 500 characters")
        String description

) {}