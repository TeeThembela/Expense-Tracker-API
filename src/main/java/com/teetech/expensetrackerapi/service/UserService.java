package com.teetech.expensetrackerapi.service;

import com.teetech.expensetrackerapi.dto.UserRequestDTO;
import com.teetech.expensetrackerapi.dto.UserResponseDTO;
import com.teetech.expensetrackerapi.dto.UserUpdateDTO;
import com.teetech.expensetrackerapi.entity.User;

import java.util.UUID;

public interface UserService {
    // Create user
    UserResponseDTO createUser(UserRequestDTO dto);

    // Update user
    UserResponseDTO updateUser(UserUpdateDTO dto, UUID userId);

    // Retrieve user
    UserResponseDTO getUser(String email);

    // Delete user  (deletes everything - profile, expenses, budgets, categories)
    void deleteUser(UUID userId);

    //Helper Methods
        //Retrieve user or throw exception
        User findUserOrThrow(UUID userId);
}
