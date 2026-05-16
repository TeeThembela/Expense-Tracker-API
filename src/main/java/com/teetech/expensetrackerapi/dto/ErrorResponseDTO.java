package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(name = "ErrorResponse", description = "Standard error envelope returned for all 4xx and 5xx responses")
public record ErrorResponseDTO(

        @Schema(description = "Short error category label", example = "Bad Request")
        String error,

        @Schema(description = "HTTP status code", example = "400")
        int errorCode,

        @Schema(description = "Human-readable explanation of what went wrong", example = "Validation failed for one or more fields")
        String message,

        @Schema(description = "Request path that triggered the error", example = "/api/v1/users/3fa85f64/expenses")
        String path,

        @Schema(description = "UTC timestamp when the error occurred", example = "2025-05-10T08:15:30")
        LocalDateTime timestamp,

        @Schema(description = "Field-level validation errors — present only on 400 responses. " +
                "Keys are field names; values are the constraint violation messages.",
                example = "{\"amount\": \"Amount must be greater than zero\", \"expenseDate\": \"Expense Date is required\"}",
                nullable = true)
        Map<String, String> details

) {
    public ErrorResponseDTO {
        if (timestamp == null) timestamp = LocalDateTime.now();
    }
}