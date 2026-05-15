package com.teetech.expensetrackerapi.service.impl;

import com.teetech.expensetrackerapi.dto.CategoryRequestDTO;
import com.teetech.expensetrackerapi.dto.CategoryResponseDTO;
import com.teetech.expensetrackerapi.dto.CategoryUpdateDTO;
import com.teetech.expensetrackerapi.entity.Category;
import com.teetech.expensetrackerapi.entity.User;
import com.teetech.expensetrackerapi.enums.CategoryType;
import com.teetech.expensetrackerapi.exception.*;
import com.teetech.expensetrackerapi.mapper.CategoryMapper;
import com.teetech.expensetrackerapi.repository.CategoryRepository;
import com.teetech.expensetrackerapi.service.CategoryService;
import com.teetech.expensetrackerapi.service.UserService;
import com.teetech.expensetrackerapi.validation.CategoryValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryValidator validator;
    private final CategoryRepository repository;
    private final CategoryMapper mapper;
    private final UserService userService;

    // Create a category for specific user
    @Transactional
    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO dto, UUID userId) {
        log.info("Creating category for userId: {}", userId);

        // Validate the business rules before doing anything
        List<String> validationErrors = validator.validateCategoryCreationRequest(dto);
        if (!validationErrors.isEmpty()){
            log.warn("Category creation failed validation for userId: {} — {} error(s)",
                    userId, validationErrors.size());
            throw new ValidationException(validationErrors);
        }

        // Check for name conflicts (user-level and system-level)
        validateCategoryName(userId, dto.name());

        // Resolve relationships
        User user = userService.findUserOrThrow(userId);

        // Map and wire relationships
        Category category = mapper.toCategory(dto);
        category.setUser(user);
        category.setType(CategoryType.USER);

        // Persist
        Category saved = repository.saveAndFlush(category);
        log.info("Category created successfully: categoryId={}, name={}, type={}, userId={}",
                saved.getId(), saved.getName(), saved.getType(), userId);

        // Map to DTO and return
        return mapper.toCategoryDTO(saved);
    }

    // Update a category
    @Transactional
    @Override
    public CategoryResponseDTO updateCategory(UUID categoryId, CategoryUpdateDTO dto, UUID userId) {
        log.info("Updating category: categoryId={}, userId={}", categoryId, userId);

        // Validate business rules
        List<String> validationErrors = validator.validateCategoryUpdate(dto);
        if (!validationErrors.isEmpty()){
            log.warn("Category update failed validation: categoryId: {} — {} error(s)", categoryId,
                    validationErrors.size());
            throw new ValidationException(validationErrors);
        }

        // Fetch the category and verify ownership
        Category category = findAccessibleCategory(categoryId, userId);

        // Block modifications to system categories
        requireUserCategory(category, "updated");


        // If name is being changed, check it won't conflict
        if (dto.name() != null && !dto.name().equalsIgnoreCase(category.getName())) {
            validateCategoryName(userId, dto.name());
        }

        // Apply partial updates (null fields are ignored by MapStruct)
        mapper.toCategoryUpdate(dto, category);

        // Persist
        Category saved = repository.saveAndFlush(category);
        log.info("Category updated successfully: categoryId={}, userId={}", categoryId, userId);

        // Map and return
        return mapper.toCategoryDTO(saved);
    }

    // Get a category
    @Transactional(readOnly = true)
    @Override
    public CategoryResponseDTO getCategory(UUID categoryId, UUID userId) {
        log.debug("Fetching category: categoryId: {}, userId: {}", categoryId, userId);

        // Verify user exists
        userService.findUserOrThrow(userId);

        // Fetch and verify ownership
        Category category = findAccessibleCategory(categoryId, userId);
        return mapper.toCategoryDTO(category);
    }

    // Get categories
    @Transactional(readOnly = true)
    @Override
    public Page<CategoryResponseDTO> getCategories(UUID userId, Pageable pageable) {
        log.debug("Fetching categories: userId: {}, page: {}, size: {}", userId, pageable.getPageNumber(),
                pageable.getPageSize());

        // Verify user exists
        userService.findUserOrThrow(userId);

        // Fetch both SYSTEM TYPE and this user's USER categories
        return repository.findCustomAndNonCustom(userId, CategoryType.SYSTEM, pageable)
                .map(mapper::toCategoryDTO);
    }

    // Delete a category
    @Transactional
    @Override
    public void deleteCategory(UUID categoryId, UUID userId) {
        log.info("Deleting category: categoryId={}, userId={}", categoryId, userId);

        // Verify ownership before deleting
        Category category = findAccessibleCategory(categoryId, userId);

        // Block deletion of system categories
        requireUserCategory(category, "deleted");

        repository.delete(category);
        log.info("Category deleted successfully: expenseId={}, userId={}", categoryId, userId);
    }

    // Helper method
        /**
         * Finds a category accessible to the user.
         * Returns the category if it belongs to the user (USER type)
         * or if it is a system category (SYSTEM type) available to all users.
         * Throws CategoryNotFoundException if neither condition is met.
         */
        @Transactional(readOnly = true)
        @Override
        public Category findAccessibleCategory(UUID categoryId, UUID userId){
            log.debug("Resolving accessible category: categoryId={}, userId={}", categoryId, userId);

            // First try user's own custom category
            return repository.findByIdAndUserId(categoryId,userId)
                    .or(() -> repository.findById(categoryId)
                            .filter(Category::isSystemCategory))
                    .orElseThrow(() -> {
                        log.warn("Category not found or not accessible: categoryId={}, userId={}", categoryId, userId);
                        return new CategoryNotFoundException(categoryId.toString());
                    });
        }

        /**
         * Checks that no existing category (user-owned) shares this name.
         * Prevents duplicate names within the user's own categories
         */
        public void validateCategoryName(UUID userId, String name){
            log.debug("Checking for duplicate category name: name={}, userId={}", name, userId);

             boolean hasUserDuplicate = repository.existsByUserIdAndNameIgnoreCase(userId, name);

             if (hasUserDuplicate){
                 log.warn("Duplicate user category name detected: name={}, userId={}", name, userId);
                 throw new DuplicateCategoryException(
                         "A category with this name already exists. Name", name);
             }
        }

        /**
         * Guards write operations (update, delete) against system categories.
         * System categories are shared and immutable — only user-defined categories
         * can be modified or deleted.
         */
        private void requireUserCategory(Category category, String operation) {
            if (category.isSystemCategory()) {
                log.warn("Attempt to {} system category: categoryId={}, name={}",
                        operation, category.getId(), category.getName());
                throw new ExpenseServiceException(
                        "System category '" + category.getName() + "' cannot be " + operation + ".",
                        "SYSTEM_CATEGORY_IMMUTABLE",
                        "Operation not permitted on system categories"
                );
            }
        }
}
