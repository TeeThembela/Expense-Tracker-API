package com.teetech.expensetrackerapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserProfileUpdateDTO(
    //User Profile id will come from path variable

    // Temporary - will be removed when security is added from security context later
    UUID userId,

    @Size(max = 100, message = "First name cannot exceed 100 characters")
    String firstName,

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    String lastName,

    @Size(min = 10, max = 10, message = "Phone number must be 10 numbers")
    String phoneNumber,

    @Size(max = 100, message = "Display name cannot exceed 100 characters")
    String displayName
) {
}
