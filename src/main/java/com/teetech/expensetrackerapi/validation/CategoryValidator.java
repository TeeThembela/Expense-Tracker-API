package com.teetech.expensetrackerapi.validation;

import com.teetech.expensetrackerapi.dto.CategoryRequestDTO;
import com.teetech.expensetrackerapi.dto.CategoryUpdateDTO;
import com.teetech.expensetrackerapi.enums.PredefinedCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CategoryValidator {
    // Reserved names that users cannot use (matches SYSTEM categories)
    private static final Set<String> RESERVED_NAMES = Arrays.stream(PredefinedCategory.values())
            .map(pc -> pc.getDisplayName().toLowerCase())
            .collect(Collectors.toSet());

    // Validate category creation request
    public List<String> validateCategoryCreationRequest(CategoryRequestDTO dto){
        log.debug("Validating category creation request: name={}, descriptionLength={}",
                dto.name(), dto.description() != null ? dto.description().length() : 0);

        List<String> errors = validateCategoryRequest(dto.name(), dto.description());

        if (!errors.isEmpty()) {
            log.warn("Category creation validation failed with {} error(s): {}", errors.size(), errors);
        } else {
            log.debug("Category creation validation passed for name: {}", dto.name());
        }

        return new ArrayList<>(errors);
    }

    // Validate category update request
    public List<String> validateCategoryUpdate(CategoryUpdateDTO dto){
        log.debug("Validating category update request: name={}, descriptionProvided={}",
                dto.name(), dto.description() != null);

        List<String> errors = new ArrayList<>();

        // Check if at least one field is being updated
        if(dto.name() == null && dto.description() == null){
            errors.add("At least one field must be provided for update");
            log.warn("Category update validation failed: No fields provided for update");
            return errors;
        }

        errors.addAll(validateCategoryRequest(dto.name(), dto.description()));

        if (!errors.isEmpty()) {
            log.warn("Category update validation failed with {} error(s): {}", errors.size(), errors);
        } else {
            log.debug("Category update validation passed");
        }

        return errors;
    }

    private List<String> validateCategoryRequest(String name, String description){
        List<String> errors = new ArrayList<>();

        if (name != null){// Validate category name format
            if (!ValidationHelper.isValidCategoryName(name)) {
                errors.add("Category name must contain only letters, numbers, spaces, hyphens, and apostrophes");
                log.debug("Invalid category name format: {}", name);
            }

            // Check minimum length
            if (name.trim().length() < 2) {
                errors.add("Category name must be at least 2 characters long");
                log.debug("Category name too short: length={}", name.trim().length());
            }

            // Check for reserved names
            if (isReservedName(name)) {
                errors.add("Category name '" + name + "' is reserved. Please choose a different name.");
                log.warn("Attempted to use reserved category name: {}", name);
            }
        }

        // Validate description if provided
        if (description != null){
            if (!ValidationHelper.hasContent(description)){
                errors.add("Description cannot be empty if provided. Either provide meaningful content or leave it " +
                        "null.");
                log.debug("Empty description provided");
            }else if (description.trim().length() < 3){
                errors.add("Description must be at least 3 characters if provided");
                log.debug("Description too short: length={}", description.trim().length());
            }
        }

        return errors;
    }

    public boolean isReservedName(String name){
        return RESERVED_NAMES.contains(name.trim().toLowerCase());
    }
}