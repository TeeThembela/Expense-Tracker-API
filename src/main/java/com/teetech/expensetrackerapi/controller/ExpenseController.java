package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.dto.ErrorResponseDTO;
import com.teetech.expensetrackerapi.dto.ExpenseFilterCriteria;
import com.teetech.expensetrackerapi.dto.ExpenseRequestDTO;
import com.teetech.expensetrackerapi.dto.ExpenseResponseDTO;
import com.teetech.expensetrackerapi.dto.ExpenseUpdateDTO;
import com.teetech.expensetrackerapi.service.ExpenseService;
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

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Expenses", description = "Record and manage individual expense entries")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_USER')")
@RequestMapping("/api/v1/users/{userId}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Operation(
            summary = "Record a new expense",
            description = "Creates a new expense entry for the specified user. " +
                    "The expense date must be today or in the past. " +
                    "The category must belong to the same user. " +
                    "If a budget exists for the category and period, the response may include a " +
                    "`budgetWarning` message when the budget threshold is exceeded. " +
                    "Requires the authenticated user to be the owner of the `userId` path parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Expense recorded successfully",
                    content = @Content(schema = @Schema(implementation = ExpenseResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed — amount not positive, date in the future, or category missing",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Category not found or does not belong to this user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<ExpenseResponseDTO> createExpense(
            @Valid @RequestBody ExpenseRequestDTO dto,
            @Parameter(description = "UUID of the owner user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId) {

        ExpenseResponseDTO response = expenseService.createExpense(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update an existing expense",
            description = "Updates an expense entry. All fields are optional — only supplied fields are changed. " +
                    "Requires the authenticated user to be the owner of the `userId` path parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expense updated successfully",
                    content = @Content(schema = @Schema(implementation = ExpenseResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Expense or category not found, or does not belong to this user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{expenseId}")
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(
            @Valid @RequestBody ExpenseUpdateDTO dto,
            @Parameter(description = "UUID of the expense to update", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7", required = true)
            @PathVariable UUID expenseId,
            @Parameter(description = "UUID of the owner user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId) {

        ExpenseResponseDTO response = expenseService.updateExpense(expenseId, dto, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get a single expense by ID",
            description = "Returns the full details of one expense entry. " +
                    "Requires the authenticated user to be the owner of the `userId` path parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Expense found",
                    content = @Content(schema = @Schema(implementation = ExpenseResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Expense not found or does not belong to this user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{expenseId}")
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<ExpenseResponseDTO> getExpense(
            @Parameter(description = "UUID of the expense to retrieve", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7", required = true)
            @PathVariable UUID expenseId,
            @Parameter(description = "UUID of the owner user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId) {

        ExpenseResponseDTO response = expenseService.getExpense(expenseId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete an expense",
            description = "Permanently deletes an expense entry. This action cannot be undone. " +
                    "Requires the authenticated user to be the owner of the `userId` path parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Expense deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Expense not found or does not belong to this user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{expenseId}")
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<Void> deleteExpense(
            @Parameter(description = "UUID of the expense to delete", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7", required = true)
            @PathVariable UUID expenseId,
            @Parameter(description = "UUID of the owner user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId) {

        expenseService.deleteExpense(expenseId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "List expenses with optional filters",
            description = """
                    Returns a paginated list of expenses. When no filter parameters are supplied, all expenses
                    for the user are returned sorted by expense date descending.

                    **Filtering:** Any combination of the parameters below activates filtered mode.
                    - `period` — predefined window: `PAST_WEEK`, `PAST_MONTH`, `LAST_3_MONTHS`, or `CUSTOM`
                    - `categoryId` — restrict to a specific category
                    - `startDate` + `endDate` — custom date range (ISO 8601 format: `yyyy-MM-dd`). Both must be supplied together.

                    Supplying only `startDate` or only `endDate` without the other will return a 400 error.

                    **Pagination:** Supports standard Spring `Pageable` query parameters — `page`, `size`, `sort`.
                    Default page size is 20, sorted by `expenseDate` descending.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of expenses returned (may be empty)",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters — e.g. only one of startDate/endDate provided, or startDate is after endDate",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<Page<ExpenseResponseDTO>> getExpenses(
            @Parameter(description = "UUID of the owner user", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Predefined period filter. One of: PAST_WEEK, PAST_MONTH, LAST_3_MONTHS, CUSTOM", example = "PAST_MONTH")
            @RequestParam(required = false) String period,
            @Parameter(description = "Filter by category UUID", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
            @RequestParam(required = false) UUID categoryId,
            @Parameter(description = "Start of custom date range (yyyy-MM-dd). Required if endDate is provided.", example = "2025-01-01")
            @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "End of custom date range (yyyy-MM-dd). Required if startDate is provided.", example = "2025-01-31")
            @RequestParam(required = false) LocalDate endDate,
            @PageableDefault(size = 20, sort = "expenseDate", direction = Sort.Direction.DESC)
            Pageable pageable) {

        if (period != null || categoryId != null || startDate != null || endDate != null) {
            ExpenseFilterCriteria filter = new ExpenseFilterCriteria(startDate, endDate, categoryId, period);
            Page<ExpenseResponseDTO> filtered = expenseService.getExpensesFiltered(userId, filter, pageable);
            return ResponseEntity.ok(filtered);
        }

        Page<ExpenseResponseDTO> response = expenseService.getExpenses(userId, pageable);
        return ResponseEntity.ok(response);
    }
}