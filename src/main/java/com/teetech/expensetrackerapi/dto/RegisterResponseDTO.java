package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RegisterResponse", description = "Confirmation message returned after successful registration")
public record RegisterResponseDTO(

        @Schema(description = "Human-readable confirmation message",
                example = "Registration successful.")
        String message

) {}