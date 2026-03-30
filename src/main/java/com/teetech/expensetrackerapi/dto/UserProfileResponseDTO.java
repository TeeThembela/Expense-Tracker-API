package com.teetech.expensetrackerapi.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileResponseDTO(
        UUID id,
        String firstName,
        String lastName,
        String phoneNumber,
        String displayName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
