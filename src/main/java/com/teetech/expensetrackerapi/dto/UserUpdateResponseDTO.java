package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserUpdateResponse", description = "Confirmation returned after updating account credentials")
public record UserUpdateResponseDTO(

        @Schema(description = "Human-readable confirmation message", example = "Account updated successfully.")
        String message

) {}