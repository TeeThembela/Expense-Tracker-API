package com.teetech.expensetrackerapi.repository;

import com.teetech.expensetrackerapi.entity.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    //Find all by user id
    Page<Budget> findAllByUserId(UUID userId, Pageable pageable);

    //Find budget and ensure user owns it
    Optional<Budget> findByIdAndUserId(UUID budgetId, UUID userId);

    //Find all by category id and user id
    Page<Budget> findAllByCategoryIdAndUserId(UUID categoryId, UUID userId, Pageable pageable);

    //Filter active budgets (where current date is within budget period)
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
            "AND :currentDate >= b.startDate " +
            "AND (b.endDate IS NULL OR :currentDate <= b.endDate)")
    Page<Budget> findActiveBudgets(
            @Param("userId") UUID userId,
            @Param("currentDate")LocalDate currentDate,
            Pageable pageable
    );

    //Filter by startDate/endDate
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
            "AND b.startDate <= :endDate " +
            "AND (b.endDate IS NULL OR b.endDate >= :startDate)")
    Page<Budget> findByUserIdAndDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    //Get budgets ordered by start date (most recent first)
    Page<Budget> findByUserIdOrderByStartDateDesc(UUID userId, Pageable pageable);
}
