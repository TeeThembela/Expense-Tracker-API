package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(name = "UserResponse", description = "Core account information for a user")
public record UserResponseDTO(

        @Schema(description = "Unique identifier of the user account", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Registered email address", example = "jane.doe@email.com")
        String email,

        @Schema(description = "UTC timestamp when the account was created", example = "2025-01-15T09:30:00")
        LocalDateTime createdAt,

        @Schema(description = "UTC timestamp of the last account update", example = "2025-01-15T09:30:00")
        LocalDateTime updatedAt

) {}