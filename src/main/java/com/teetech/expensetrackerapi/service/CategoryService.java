package com.teetech.expensetrackerapi.service;

import com.teetech.expensetrackerapi.dto.CategoryRequestDTO;
import com.teetech.expensetrackerapi.dto.CategoryResponseDTO;
import com.teetech.expensetrackerapi.dto.CategoryUpdateDTO;
import com.teetech.expensetrackerapi.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoryService {
    // Create category
    CategoryResponseDTO createCategory(CategoryRequestDTO dto, UUID userId);

    // Update category
    CategoryResponseDTO updateCategory(UUID categoryId, CategoryUpdateDTO dto, UUID userId);

    // Retrieve a single category
    CategoryResponseDTO getCategory(UUID categoryId, UUID userId);

    // Retrieve multiple categories
    Page<CategoryResponseDTO> getCategories(UUID userId, Pageable pageable);

    // Delete category
    void deleteCategory(UUID categoryId, UUID userId);

    //Helper Methods
        //Retrieve category or throw exception
        Category findAccessibleCategory(UUID categoryId, UUID userId);
}
