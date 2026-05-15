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
    Page<Budget> findAllByUserId(UUID userId, Pageable pageable);

    Optional<Budget> findByIdAndUserId(UUID budgetId, UUID userId);

    Page<Budget> findAllByCategoryIdAndUserId(UUID categoryId, UUID userId, Pageable pageable);

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
            "AND :currentDate >= b.startDate " +
            "AND (b.endDate IS NULL OR :currentDate <= b.endDate)")
    Page<Budget> findActiveBudgets(
            @Param("userId") UUID userId,
            @Param("currentDate") LocalDate currentDate,
            Pageable pageable
    );

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
            "AND b.startDate <= :endDate " +
            "AND (b.endDate IS NULL OR b.endDate >= :startDate)")
    Page<Budget> findByUserIdAndDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("SELECT b FROM Budget b " +
            "WHERE b.user.id = :userId " +
            "AND b.category.id = :categoryId " +
            "AND b.startDate <= :expenseDate " +
            "AND (b.endDate IS NULL OR b.endDate >= :expenseDate)")
    Optional<Budget> findBudgetForCategoryOnDate(
            @Param("userId") UUID userId,
            @Param("categoryId") UUID categoryId,
            @Param("expenseDate") LocalDate expenseDate
    );

    Page<Budget> findByUserIdOrderByStartDateDesc(UUID userId, Pageable pageable);

    // IMPROVEMENT: Using SpEL (:#{#...}) to evaluate nulls before hitting the DB driver,
    // preventing the infamous "could not determine data type of parameter" crash.
    @Query("SELECT COUNT(b) > 0 FROM Budget b " +
            "WHERE b.user.id = :userId " +
            "AND b.category.id = :categoryId " +
            "AND (:#{#newEndDate == null} = true OR b.startDate <= :newEndDate) " +
            "AND (b.endDate IS NULL OR b.endDate >= :newStartDate)")
    boolean existsOverlappingBudget(
            @Param("userId") UUID userId,
            @Param("categoryId") UUID categoryId,
            @Param("newStartDate") LocalDate newStartDate,
            @Param("newEndDate") LocalDate newEndDate
    );

    // IMPROVEMENT: Same SpEL null-safety applied here.
    @Query("SELECT COUNT(b) > 0 FROM Budget b " +
            "WHERE b.user.id = :userId " +
            "AND b.category.id = :categoryId " +
            "AND b.id <> :excludeBudgetId " +
            "AND (:#{#newEndDate == null} = true OR b.startDate <= :newEndDate) " +
            "AND (b.endDate IS NULL OR b.endDate >= :newStartDate)")
    boolean existsOverlappingBudgetExcluding(
            @Param("userId") UUID userId,
            @Param("categoryId") UUID categoryId,
            @Param("newStartDate") LocalDate newStartDate,
            @Param("newEndDate") LocalDate newEndDate,
            @Param("excludeBudgetId") UUID excludeBudgetId
    );
}