package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.dto.CategoryRequestDTO;
import com.teetech.expensetrackerapi.dto.CategoryResponseDTO;
import com.teetech.expensetrackerapi.dto.CategoryUpdateDTO;
import com.teetech.expensetrackerapi.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/{userId}/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @PathVariable UUID userId,
           @Valid @RequestBody CategoryRequestDTO dto){

        CategoryResponseDTO response = categoryService.createCategory(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable UUID userId,
            @PathVariable UUID categoryId,
           @Valid @RequestBody CategoryUpdateDTO dto){

        CategoryResponseDTO response = categoryService.updateCategory(categoryId, dto, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> getCategory(
            @PathVariable UUID categoryId,
            @PathVariable UUID userId){

        CategoryResponseDTO response = categoryService.getCategory(categoryId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponseDTO>> getCategories(
            @PathVariable UUID userId,
            @PageableDefault(size = 10, sort = "type", direction = Sort.Direction.DESC) Pageable pageable){

        Page<CategoryResponseDTO> response = categoryService.getCategories(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable UUID categoryId,
            @PathVariable UUID userId
            ){

        categoryService.deleteCategory(categoryId, userId);
        return ResponseEntity.noContent().build();
    }

}
