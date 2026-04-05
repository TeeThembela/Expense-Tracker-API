package com.teetech.expensetrackerapi.repository;

import com.teetech.expensetrackerapi.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    //Find by id and ensure user owns it (prevents accessing other users' expenses)
    Optional<Expense> findByIdAndUserId(UUID expenseId, UUID userId);

    //Find all by user id
    Page<Expense> findByUserId(UUID userId, Pageable pageable);

    //Filter by date range - "Past week", "Past month", etc.
    @Query(
            "SELECT e FROM Expense e " +
                    "WHERE e.user.id = :userId " +
                    "AND e.expenseDate BETWEEN :startDate AND :endDate"
    )
    Page<Expense> findByUserIdAndExpenseDate(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
            );


    //Filter by category id for a specific user
    Page<Expense> findByCategoryIdAndUserId(UUID categoryId, UUID userId, Pageable pageable);

    //Filter by date and category combined
    Page<Expense> findByUserIdAndCategoryIdAndExpenseDateBetween(
            UUID userId,
            UUID categoryId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    //Get expenses ordered by date (most recent first)
    Page<Expense> findByUserIdOrderByExpenseDateDesc(UUID userId, Pageable pageable);

    //Get expenses ordered by amount (highest first)
    Page<Expense> findByUserIdOrderByAmountDesc(UUID userId, Pageable pageable);
}
