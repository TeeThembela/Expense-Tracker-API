package com.teetech.expensetrackerapi.service;

import com.teetech.expensetrackerapi.dto.BudgetRequestDTO;
import com.teetech.expensetrackerapi.dto.BudgetResponseDTO;
import com.teetech.expensetrackerapi.dto.BudgetUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BudgetService {
    // Create budget
    BudgetResponseDTO createBudget(BudgetRequestDTO dto, UUID userId);

    // Update budget
    BudgetResponseDTO updateBudget(UUID budgetId, BudgetUpdateDTO dto, UUID userId);

    // Retrieve a single budget
    BudgetResponseDTO getBudget(UUID budgetId, UUID userId);

    // Retrieve multiple budgets
    Page<BudgetResponseDTO> getBudgets(UUID userId, Pageable pageable);

    // Delete budget
    void deleteBudget(UUID budgetId, UUID userId);
}
