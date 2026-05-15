package com.teetech.expensetrackerapi.service;

import com.teetech.expensetrackerapi.dto.UserUpdateDTO;
import com.teetech.expensetrackerapi.dto.UserUpdateResponseDTO;
import com.teetech.expensetrackerapi.entity.User;

import java.util.UUID;

public interface UserService {
    // Delete user  (deletes everything - profile, expenses, budgets, categories)
    void deleteUser(UUID userId);

    // Disable account (By Admin or Manager)
    void disableAccount(UUID userId);

    // Enable account
    void endableAccount(UUID userId);

    // Update the user (email or password)
    UserUpdateResponseDTO updateUser(UserUpdateDTO request, UUID userId);

    //Helper Methods
        //Retrieve user or throw exception
        User findUserOrThrow(UUID userId);
}
