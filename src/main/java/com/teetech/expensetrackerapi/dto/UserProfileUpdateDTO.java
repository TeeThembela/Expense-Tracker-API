package com.teetech.expensetrackerapi.dto;

import jakarta.validation.constraints.Size;

public record UserProfileUpdateDTO(

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
