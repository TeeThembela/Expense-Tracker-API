package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.dto.BudgetRequestDTO;
import com.teetech.expensetrackerapi.dto.BudgetResponseDTO;
import com.teetech.expensetrackerapi.dto.BudgetUpdateDTO;
import com.teetech.expensetrackerapi.service.BudgetService;
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
@RequestMapping("/api/v1/users/{userId}/budgets")
@AllArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponseDTO> createBudget(
            @PathVariable UUID userId,
            @Valid @RequestBody BudgetRequestDTO dto) {

        BudgetResponseDTO response = budgetService.createBudget(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponseDTO> updateBudget(
            @PathVariable UUID userId,
            @PathVariable UUID budgetId,
            @Valid @RequestBody BudgetUpdateDTO dto) {

        BudgetResponseDTO response = budgetService.updateBudget(budgetId, dto, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponseDTO> getBudget(
            @PathVariable UUID userId,
            @PathVariable UUID budgetId) {

        BudgetResponseDTO response = budgetService.getBudget(budgetId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<BudgetResponseDTO>> getBudgets(
            @PathVariable UUID userId,
            @PageableDefault(size = 10, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<BudgetResponseDTO> response = budgetService.getBudgets(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable UUID userId,
            @PathVariable UUID budgetId) {

        budgetService.deleteBudget(budgetId, userId);
        return ResponseEntity.noContent().build();
    }
}
