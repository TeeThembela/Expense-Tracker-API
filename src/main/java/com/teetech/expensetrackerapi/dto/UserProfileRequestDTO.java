package com.teetech.expensetrackerapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UserProfileRequestDTO(

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name cannot exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name cannot exceed 100 characters")
        String lastName,

        @NotBlank(message = "Phone number is required")
        @Size(min = 10, max = 10, message = "Phone number must be 10 numbers")
        String phoneNumber,

        //If not provided, use the first name as display name
        @Size(max = 100, message = "Display name cannot exceed 100 characters")
        String displayName
) {
}
