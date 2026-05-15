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

    //Find by category id and ensure user owns it (e.g., for delete/update)
    Optional<Category> findByIdAndUserId(UUID categoryId, UUID userId);

    //Filter by type OR user
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId OR c.type = :systemType")
    Page<Category> findCustomAndNonCustom(
            @Param("userId") UUID userId,
            @Param("systemType") CategoryType systemType,
            Pageable pageable
    );

    //Check if user already has a category with this name (prevent duplicates)
    boolean existsByUserIdAndNameIgnoreCase(UUID userId, String name);

}
