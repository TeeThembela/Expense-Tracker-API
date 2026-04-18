package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.dto.ExpenseFilterCriteria;
import com.teetech.expensetrackerapi.dto.ExpenseRequestDTO;
import com.teetech.expensetrackerapi.dto.ExpenseResponseDTO;
import com.teetech.expensetrackerapi.dto.ExpenseUpdateDTO;
import com.teetech.expensetrackerapi.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users/{userId}/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> createExpense(
            @Valid @RequestBody ExpenseRequestDTO dto,
            @PathVariable UUID userId){

        ExpenseResponseDTO response = expenseService.createExpense(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(
            @Valid @RequestBody ExpenseUpdateDTO dto,
            @PathVariable UUID expenseId,
            @PathVariable UUID userId
            ){

        ExpenseResponseDTO response = expenseService.updateExpense(expenseId,dto, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDTO> getExpense(
            @PathVariable UUID expenseId,
            @PathVariable UUID userId
            ){

        ExpenseResponseDTO response = expenseService.getExpense(expenseId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable UUID expenseId,
            @PathVariable UUID userId
            ){
        expenseService.deleteExpense(expenseId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ExpenseResponseDTO>> getExpenses(
            @PathVariable UUID userId,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @PageableDefault(size = 20, sort = "expenseDate", direction = Sort.Direction.DESC)
            Pageable pageable){

        // If any filter param is present, route to filtered query
        if (period != null || categoryId != null || startDate != null || endDate != null) {
            ExpenseFilterCriteria filter = new ExpenseFilterCriteria(startDate, endDate, categoryId, period);
            Page<ExpenseResponseDTO> filtered = expenseService.getExpensesFiltered(userId, filter, pageable);
            return ResponseEntity.ok(filtered);
        }

        Page<ExpenseResponseDTO> response = expenseService.getExpenses(userId, pageable);
        return ResponseEntity.ok(response);
    }


}
