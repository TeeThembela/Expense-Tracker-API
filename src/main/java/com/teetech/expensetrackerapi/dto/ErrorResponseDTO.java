package com.teetech.expensetrackerapi.dto;

import java.time.LocalDateTime;
import java.util.Map;


public record ErrorResponseDTO(
        String error,
        int errorCode,
        String message,
        String path,
        LocalDateTime timestamp,
        Map<String, String> details
) {
    public ErrorResponseDTO {
        if (timestamp == null ) timestamp = LocalDateTime.now();
    }
}
