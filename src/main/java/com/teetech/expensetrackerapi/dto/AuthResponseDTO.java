package com.teetech.expensetrackerapi.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record AuthResponseDTO(
        String accessToken,
        String tokenType,
        Instant expireIn
) {
}
