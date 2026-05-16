package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.dto.BudgetRequestDTO;
import com.teetech.expensetrackerapi.dto.BudgetResponseDTO;
import com.teetech.expensetrackerapi.dto.BudgetUpdateDTO;
import com.teetech.expensetrackerapi.dto.ErrorResponseDTO;
import com.teetech.expensetrackerapi.service.BudgetService;
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

@Tag(name = "Budgets", description = "Create and manage spending budgets per category and period")
@RestController
@PreAuthorize("hasAuthority('ROLE_USER')")
@RequestMapping("/api/v1/users/{userId}/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @Operation(
            summary = "Create a new budget",
            description = "Creates a budget for a specific category and date range. " +
                    "Requires the authenticated user to be the owner of the `userId` path parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Budget created successfully",
                    content = @Content(schema = @Schema(implementation = BudgetResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed — amount is not positive, start date missing, or end date is before start date",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated — valid JWT access token required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner of this userId",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Category not found for this user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<BudgetResponseDTO> createBudget(
            @Parameter(description = "UUID of the owner user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId,
            @Valid @RequestBody BudgetRequestDTO dto) {

        BudgetResponseDTO response = budgetService.createBudget(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update an existing budget",
            description = "Partially updates a budget. All fields are optional — only supplied fields are changed. " +
                    "Requires the authenticated user to be the owner of the `userId` path parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget updated successfully",
                    content = @Content(schema = @Schema(implementation = BudgetResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed — amount not positive or date range invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Budget not found or does not belong to this user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{budgetId}")
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<BudgetResponseDTO> updateBudget(
            @Parameter(description = "UUID of the owner user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "UUID of the budget to update", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7", required = true)
            @PathVariable UUID budgetId,
            @Valid @RequestBody BudgetUpdateDTO dto) {

        BudgetResponseDTO response = budgetService.updateBudget(budgetId, dto, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get a single budget by ID",
            description = "Returns the full details of one budget. " +
                    "Requires the authenticated user to be the owner of the `userId` path parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget found",
                    content = @Content(schema = @Schema(implementation = BudgetResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Budget not found or does not belong to this user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{budgetId}")
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<BudgetResponseDTO> getBudget(
            @Parameter(description = "UUID of the owner user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "UUID of the budget to retrieve", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7", required = true)
            @PathVariable UUID budgetId) {

        BudgetResponseDTO response = budgetService.getBudget(budgetId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List all budgets for a user",
            description = "Returns a paginated list of all budgets belonging to the specified user, " +
                    "sorted by start date descending by default. " +
                    "Supports standard Spring `Pageable` query parameters: `page`, `size`, `sort`."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of budgets returned (may be empty)",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<Page<BudgetResponseDTO>> getBudgets(
            @Parameter(description = "UUID of the owner user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId,
            @PageableDefault(sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<BudgetResponseDTO> response = budgetService.getBudgets(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a budget",
            description = "Permanently deletes a budget. This action cannot be undone. " +
                    "Requires the authenticated user to be the owner of the `userId` path parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Budget deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Budget not found or does not belong to this user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{budgetId}")
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<Void> deleteBudget(
            @Parameter(description = "UUID of the owner user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "UUID of the budget to delete", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7", required = true)
            @PathVariable UUID budgetId) {

        budgetService.deleteBudget(budgetId, userId);
        return ResponseEntity.noContent().build();
    }
}