package com.teetech.expensetrackerapi.repository;

import com.teetech.expensetrackerapi.entity.Category;
import com.teetech.expensetrackerapi.enums.CategoryType;
import com.teetech.expensetrackerapi.enums.PredefinedCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    //DataSeeder needs this to check if SYSTEM categories exist!
    long countByType(CategoryType type);

    //Find by category id and ensure user owns it (e.g., for delete/update)
    Optional<Category> findByIdAndUserId(UUID categoryId, UUID userId);

    //Find by name and user id
    Optional<Category> findByUserIdAndNameIgnoreCase(UUID userId, String name);


    //Filter by type OR user
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId OR c.type = :systemType")
    Page<Category> findCustomAndNonCustom(
            @Param("userId") UUID userId,
            @Param("systemType") CategoryType systemType,
            Pageable pageable
    );

    //Filter by type AND user id (Custom categories)
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId AND c.type = :type")
    Page<Category> findCustom(
            @Param("userId") UUID userId,
            @Param("type") String type,
            Pageable pageable
    );

    //Find a specific system category
    Optional<Category> findByPredefinedCategory(PredefinedCategory predefinedCategory);

    //Check if user already has a category with this name (prevent duplicates)
    boolean existsByUserIdAndNameIgnoreCase(UUID userId, String name);

    // Check if a SYSTEM category with this name exists
    boolean existsByTypeAndNameIgnoreCase(CategoryType type, String name);
}
