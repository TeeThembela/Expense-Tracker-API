package com.teetech.expensetrackerapi.service;

import com.teetech.expensetrackerapi.dto.UserProfileRequestDTO;
import com.teetech.expensetrackerapi.dto.UserProfileResponseDTO;
import com.teetech.expensetrackerapi.dto.UserProfileUpdateDTO;

import java.util.UUID;

public interface UserProfileService {
    // Create user profile
    UserProfileResponseDTO createUserProfile(UserProfileRequestDTO dto, UUID userId);

    // Update user profile
    UserProfileResponseDTO updateUserProfile(UserProfileUpdateDTO dto, UUID userId);

    // Retrieve user profile
    UserProfileResponseDTO getUserProfile(UUID userId);
}
