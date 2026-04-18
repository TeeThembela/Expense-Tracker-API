package com.teetech.expensetrackerapi.service.impl;

import com.teetech.expensetrackerapi.dto.UserProfileRequestDTO;
import com.teetech.expensetrackerapi.dto.UserProfileResponseDTO;
import com.teetech.expensetrackerapi.dto.UserProfileUpdateDTO;
import com.teetech.expensetrackerapi.entity.User;
import com.teetech.expensetrackerapi.entity.UserProfile;
import com.teetech.expensetrackerapi.exception.DuplicateUserProfileException;
import com.teetech.expensetrackerapi.exception.UserProfileNotFoundException;
import com.teetech.expensetrackerapi.exception.ValidationException;
import com.teetech.expensetrackerapi.mapper.UserProfileMapper;
import com.teetech.expensetrackerapi.repository.UserProfileRepository;
import com.teetech.expensetrackerapi.service.UserProfileService;
import com.teetech.expensetrackerapi.service.UserService;
import com.teetech.expensetrackerapi.validation.UserProfileValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileMapper mapper;
    private final UserProfileRepository repository;
    private final UserProfileValidator validator;
    private final UserService userService;

    // Create user profile
    @Transactional
    @Override
    public UserProfileResponseDTO createUserProfile(UserProfileRequestDTO dto, UUID userId) {
        log.info("Creating user profile for userId: {}", userId);

        // Validate business rules before doing anything else
        List<String> validationErrors = validator.validateUserProfileCreationRequest(dto);
        if (!validationErrors.isEmpty()) {
            log.warn("User profile creation failed validation for userId: {} — {} error(s)",
                    userId, validationErrors.size());
            throw new ValidationException(validationErrors);
        }

        // Ensure user exists
        User user = userService.findUserOrThrow(userId);
        log.debug("User resolved: userId={}, email={}", user.getId(), user.getEmail());

        // Prevent duplicate profile
        if (repository.existsByUserId(userId)) {
            log.warn("Duplicate profile creation attempt for userId: {}", userId);
            throw new DuplicateUserProfileException(userId.toString());
        }

        // Map and wire relationship
        UserProfile userProfile = mapper.toUserProfile(dto);
        userProfile.setUser(user);

        // Set displayName fallback: use firstName if displayName not provided
        if (userProfile.getDisplayName() == null || userProfile.getDisplayName().isBlank()) {
            log.debug("No display name provided — defaulting to firstName: {}", dto.firstName());
            userProfile.setDisplayName(dto.firstName());
        }

        // Persist
        UserProfile saved = repository.saveAndFlush(userProfile);
        log.info("User profile created successfully: profileId={}, userId={}", saved.getId(), userId);

        return mapper.toUserProfileDTO(saved);
    }

    // Update user profile
    @Transactional
    @Override
    public UserProfileResponseDTO updateUserProfile(UserProfileUpdateDTO dto, UUID userId) {

        log.info("Updating user profile for userId: {}", userId);

        // Validate business rules
        List<String> validationErrors = validator.validateUserProfileUpdateRequest(dto);
        if (!validationErrors.isEmpty()) {
            log.warn("User profile update failed validation for userId: {} — {} error(s)",
                    userId, validationErrors.size());
            throw new ValidationException(validationErrors);
        }

        // Fetch Existing Entity
        UserProfile userProfile = findUserProfileOrThrow(userId);
        log.debug("Existing profile found: profileId={}", userProfile.getId());

        // Apply partial updates (null fields are ignored by MapStruct)
        UserProfile updatedProfile = mapper.toUserProfileUpdate(dto, userProfile);

        // Persist
        UserProfile saved = repository.saveAndFlush(updatedProfile);
        log.info("User profile updated successfully: profileId={}, userId={}", saved.getId(), userId);

        return mapper.toUserProfileDTO(saved);
    }

    // Get user profile by user id
    @Transactional(readOnly = true)
    @Override
    public UserProfileResponseDTO getUserProfile(UUID userId) {
        log.debug("Fetching user profile for userId: {}", userId);

        //Find the user profile
        UserProfile userProfile = findUserProfileOrThrow(userId);
        log.debug("User profile found: profileId={}, userId={}", userProfile.getId(), userId);

        // Map to DTO and return
        return mapper.toUserProfileDTO(userProfile);
    }

    //Helper Methods
        /**
         * Fetches a user profile by userId.
         * Throws UserProfileNotFoundException if no profile exists for that user.
        */
        @Transactional(readOnly = true)
        protected UserProfile findUserProfileOrThrow(UUID userId){
        log.debug("Fetching user profile by userId: {}", userId);
        return repository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("User profile not found for userId: {}", userId);
                    return new UserProfileNotFoundException(userId.toString());
                });
        }




}
