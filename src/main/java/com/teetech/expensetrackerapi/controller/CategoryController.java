package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.dto.CategoryRequestDTO;
import com.teetech.expensetrackerapi.dto.CategoryResponseDTO;
import com.teetech.expensetrackerapi.dto.CategoryUpdateDTO;
import com.teetech.expensetrackerapi.dto.ErrorResponseDTO;
import com.teetech.expensetrackerapi.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Categories", description = "Manage expense categories for a user")
@RestController
@RequestMapping("/api/v1/users/{userId}/categories")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_USER')")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Create a new category",
            description = "Creates a spending category for the specified user. " +
                    "Category names are user-scoped — the same name can exist for different users. " +
                    "Requires the authenticated user to be the owner of the `userId` path parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created successfully",
                    content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed — name is blank or exceeds 100 characters",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Parameter(description = "UUID of the owner user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId,
            @Valid @RequestBody CategoryRequestDTO dto) {

        CategoryResponseDTO response = categoryService.createCategory(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update an existing category",
            description = "Updates a category's name and/or description. Both fields are optional — " +
                    "omit a field to leave it unchanged. " +
                    "Requires the authenticated user to be the owner of the `userId` path parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully",
                    content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed — name or description exceeds character limit",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Category not found or does not belong to this user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{categoryId}")
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @Parameter(description = "UUID of the owner user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "UUID of the category to update", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7", required = true)
            @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryUpdateDTO dto) {

        CategoryResponseDTO response = categoryService.updateCategory(categoryId, dto, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get a single category by ID",
            description = "Returns the details of one category. " +
                    "Requires the authenticated user to be the owner of the `userId` path parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category found",
                    content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Category not found or does not belong to this user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{categoryId}")
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<CategoryResponseDTO> getCategory(
            @Parameter(description = "UUID of the category to retrieve", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7", required = true)
            @PathVariable UUID categoryId,
            @Parameter(description = "UUID of the owner user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId) {

        CategoryResponseDTO response = categoryService.getCategory(categoryId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List all categories for a user",
            description = "Returns a paginated list of all categories belonging to the specified user, " +
                    "sorted by type descending by default. " +
                    "Supports standard Spring `Pageable` query parameters: `page`, `size`, `sort`."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of categories returned (may be empty)",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<Page<CategoryResponseDTO>> getCategories(
            @Parameter(description = "UUID of the owner user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId,
            @PageableDefault(sort = "type", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<CategoryResponseDTO> response = categoryService.getCategories(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a category",
            description = "Permanently deletes a category. This action cannot be undone. " +
                    "Note: deleting a category that is referenced by expenses or budgets may result " +
                    "in a constraint error depending on the cascade configuration. " +
                    "Requires the authenticated user to be the owner of the `userId` path parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Category not found or does not belong to this user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "UUID of the category to delete", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7", required = true)
            @PathVariable UUID categoryId,
            @Parameter(description = "UUID of the owner user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId) {

        categoryService.deleteCategory(categoryId, userId);
        return ResponseEntity.noContent().build();
    }
}