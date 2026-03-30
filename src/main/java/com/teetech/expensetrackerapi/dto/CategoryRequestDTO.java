package com.teetech.expensetrackerapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CategoryRequestDTO (
        // Temporary - will be removed when security is added
        @NotNull(message = "User Id is required")
        UUID userId,

        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name cannot exceed 100 characters")
        String name,

        @Size(max = 500, message = "Category description cannot exceed 500 characters")
        String description
) {
}
