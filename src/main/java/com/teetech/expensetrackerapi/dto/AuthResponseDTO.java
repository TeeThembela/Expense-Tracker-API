package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.Instant;

@Schema(name = "AuthResponse", description = "JWT access token returned after a successful login or token refresh")
@Builder
public record AuthResponseDTO(

        @Schema(description = "Short-lived JWT access token. Include this in the Authorization header as: Bearer <token>",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "Token type — always 'Bearer'",
                example = "Bearer")
        String tokenType,

        @Schema(description = "UTC timestamp at which the access token expires",
                example = "2025-06-01T10:30:00Z")
        Instant expireIn

) {}