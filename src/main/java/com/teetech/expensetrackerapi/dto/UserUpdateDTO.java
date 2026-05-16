package com.teetech.expensetrackerapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(name = "UserUpdate",
        description = "Partial update payload for user account credentials — all fields are optional")
public record UserUpdateDTO(

        @Schema(description = "New email address — must be unique across all accounts",
                example = "jane.new@email.com")
        @Email(message = "Email should be valid")
        String email,

        @Schema(description = "New password — must be between 8 and 128 characters",
                example = "NewS3cur3P@ss",
                minLength = 8,
                maxLength = 128)
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        String password

) {
        public UserUpdateDTO {
                if (email == null) email = "";
        }
}