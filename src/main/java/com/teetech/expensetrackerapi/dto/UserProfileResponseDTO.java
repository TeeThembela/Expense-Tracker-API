package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(name = "UserProfileResponse", description = "Personal profile details for a user account")
public record UserProfileResponseDTO(

        @Schema(description = "Unique identifier of the profile record", example = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d")
        UUID id,

        @Schema(description = "First name", example = "Jane")
        String firstName,

        @Schema(description = "Last name", example = "Doe")
        String lastName,

        @Schema(description = "Phone number", example = "0821234567")
        String phoneNumber,

        @Schema(description = "Display name shown in the UI", example = "Jane D")
        String displayName,

        @Schema(description = "UTC timestamp when the profile was created", example = "2025-01-15T09:35:00")
        LocalDateTime createdAt,

        @Schema(description = "UTC timestamp of the last profile update", example = "2025-03-20T14:00:00")
        LocalDateTime updatedAt

) {}