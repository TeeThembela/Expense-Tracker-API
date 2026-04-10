package com.teetech.expensetrackerapi.validation;

import com.teetech.expensetrackerapi.dto.UserProfileRequestDTO;
import com.teetech.expensetrackerapi.dto.UserProfileUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserProfileValidator {

    // Validate user profile creation request
    public  List<String> validateUserProfileCreationRequest(UserProfileRequestDTO dto){
        log.debug("Validating user profile creation request: firstName={}, lastName={}, phoneNumber={}",
                dto.firstName(), dto.lastName(), dto.phoneNumber());

        List<String> errors = validateUserProfileRequest(dto.firstName(), dto.lastName(), dto.phoneNumber(),
                dto.displayName());

        if (!errors.isEmpty()) {
            log.warn("User profile creation validation failed with {} error(s): {}", errors.size(), errors);
        } else {
            log.debug("User profile creation validation passed");
        }

        return new ArrayList<>(errors);
    }

    // Validate user profile update request
    public List<String> validateUserProfileUpdateRequest(UserProfileUpdateDTO dto){
        log.debug("Validating user profile update request: firstNameProvided={}, lastNameProvided={}, phoneProvided={}",
                dto.firstName() != null, dto.lastName() != null, dto.phoneNumber() != null);

        List<String> errors = new ArrayList<>();

        // Check if at least one field is being updated
        if (dto.firstName()== null && dto.lastName() == null && dto.phoneNumber() == null
                && dto.displayName()== null) {
            errors.add("At least one field must be provided for update");
            log.warn("User profile update validation failed: No fields provided for update");
            return errors;
        }

        errors.addAll(validateUserProfileRequest(dto.firstName(), dto.lastName(), dto.phoneNumber(),
                dto.displayName()));

        if (!errors.isEmpty()) {
            log.warn("User profile update validation failed with {} error(s): {}", errors.size(), errors);
        } else {
            log.debug("User profile update validation passed");
        }

        return errors;
    }

    private List<String> validateUserProfileRequest(String firstName, String lastName,
                                                    String phoneNumber, String displayName) {
        List<String> errors = new ArrayList<>();

        // Validate first name
        if (firstName != null && !ValidationHelper.isValidName(firstName)){
            errors.add("First name must contain only letters, spaces, hyphens, and apostrophes");
            log.debug("Invalid first name format: {}", firstName);
        }

        // Validate last name
        if (lastName != null && !ValidationHelper.isValidName(lastName)){
            errors.add("Last name must contain only letters, spaces, hyphens, and apostrophes");
            log.debug("Invalid last name format: {}", lastName);
        }

        // Validate phone number
        if (phoneNumber != null){
            if (!ValidationHelper.isValidPhoneNumber(phoneNumber)) {
                errors.add("Phone number must be 10 digits starting with 0 (e.g., 0821234567)");
                log.debug("Invalid phone number format: {}", phoneNumber);
            }
            if (!ValidationHelper.isReasonablePhoneNumber(phoneNumber)){
                errors.add("Phone number appears invalid. Please enter a valid South African phone number.");
                log.warn("Suspicious phone number detected: {}", phoneNumber);
            }
        }

        // Validate display name if provided
        if (displayName != null && !ValidationHelper.hasContent(displayName)){
            errors.add("Display name cannot be empty if provided");
            log.debug("Empty display name provided");
        }

        return errors;
    }
}