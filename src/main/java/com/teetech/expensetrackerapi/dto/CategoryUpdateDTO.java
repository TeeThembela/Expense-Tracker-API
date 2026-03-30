package com.teetech.expensetrackerapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CategoryUpdateDTO(
        //Category id will come from path variable

        // Temporary - will be removed when security is added from security context
        @NotNull(message = "Category is required")
        UUID userId,

        @Size(max = 100, message = "Category name cannot exceed 100 characters")
        String name,

        @Size(max = 500, message = "Category description cannot exceed 500 characters")
        String description
) {
}
