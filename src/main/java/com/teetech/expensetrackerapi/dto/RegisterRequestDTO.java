package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "RegisterRequest", description = "Payload required to create a new user account")
public record RegisterRequestDTO(

        @Schema(description = "Email address — must be unique across all accounts",
                example = "jane.doe@email.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @Schema(description = "Password — must be between 8 and 128 characters",
                example = "S3cur3P@ssword",
                minLength = 8,
                maxLength = 128,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        String password

) {}