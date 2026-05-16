package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "Credentials required to authenticate and obtain a JWT access token")
public record LoginRequestDTO(

        @Schema(description = "Registered email address of the user",
                example = "jane.doe@email.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @Schema(description = "Account password",
                example = "S3cur3P@ssword",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Password is required")
        String password

) {}