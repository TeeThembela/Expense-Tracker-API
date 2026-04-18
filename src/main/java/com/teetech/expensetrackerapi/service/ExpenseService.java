package com.teetech.expensetrackerapi.service;

import com.teetech.expensetrackerapi.dto.ExpenseFilterCriteria;
import com.teetech.expensetrackerapi.dto.ExpenseRequestDTO;
import com.teetech.expensetrackerapi.dto.ExpenseResponseDTO;
import com.teetech.expensetrackerapi.dto.ExpenseUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ExpenseService {
    // Create new expense
    ExpenseResponseDTO createExpense(ExpenseRequestDTO dto, UUID userId);

    // Update expense
    ExpenseResponseDTO updateExpense(UUID expenseId, ExpenseUpdateDTO dto, UUID userId);

    // Retrieve a single expense
    ExpenseResponseDTO getExpense(UUID expenseId, UUID userId);

    // Retrieve multiple expenses
    Page<ExpenseResponseDTO> getExpenses(UUID userId, Pageable pageable);

    // Filtered retrieval
    Page<ExpenseResponseDTO> getExpensesFiltered(UUID userId, ExpenseFilterCriteria filter, Pageable pageable);

    // Delete expense
    void deleteExpense(UUID expenseId, UUID userId);
}
