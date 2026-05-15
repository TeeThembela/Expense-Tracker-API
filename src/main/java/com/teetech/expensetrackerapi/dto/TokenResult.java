package com.teetech.expensetrackerapi.dto;

import java.time.Instant;

public record TokenResult(
        String token,
        Instant expiresAt
) {
}
