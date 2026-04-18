package com.teetech.expensetrackerapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateDTO(

        @Email(message = "Email should be valid")
        String email
) {
}
