package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "UserProfileRequest", description = "Payload required to create a personal profile for the authenticated user")
public record UserProfileRequestDTO(

        @Schema(description = "Legal first name — max 100 characters",
                example = "Jane",
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name cannot exceed 100 characters")
        String firstName,

        @Schema(description = "Legal last name — max 100 characters",
                example = "Doe",
                maxLength = 100,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name cannot exceed 100 characters")
        String lastName,

        @Schema(description = "Phone number — must be exactly 10 digits, no spaces or hyphens",
                example = "0821234567",
                minLength = 10,
                maxLength = 10,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Phone number is required")
        @Size(min = 10, max = 10, message = "Phone number must be 10 digits")
        String phoneNumber,

        @Schema(description = "Preferred display name shown in the UI. Defaults to firstName if omitted — max 100 characters",
                example = "Jane D",
                maxLength = 100)
        @Size(max = 100, message = "Display name cannot exceed 100 characters")
        String displayName

) {}
