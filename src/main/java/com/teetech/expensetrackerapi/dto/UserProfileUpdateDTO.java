package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(name = "UserProfileUpdate",
        description = "Partial update payload for a user profile — all fields are optional; only supplied fields are changed")
public record UserProfileUpdateDTO(

        @Schema(description = "New first name — max 100 characters", example = "Janet", maxLength = 100)
        @Size(max = 100, message = "First name cannot exceed 100 characters")
        String firstName,

        @Schema(description = "New last name — max 100 characters", example = "Doe-Smith", maxLength = 100)
        @Size(max = 100, message = "Last name cannot exceed 100 characters")
        String lastName,

        @Schema(description = "New phone number — must be exactly 10 digits", example = "0839876543",
                minLength = 10, maxLength = 10)
        @Size(min = 10, max = 10, message = "Phone number must be 10 digits")
        String phoneNumber,

        @Schema(description = "New display name — max 100 characters", example = "Janet D", maxLength = 100)
        @Size(max = 100, message = "Display name cannot exceed 100 characters")
        String displayName

) {}