package com.teetech.expensetrackerapi.repository;

import com.teetech.expensetrackerapi.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    //Find by user id
    Optional<UserProfile> findByUserId(UUID userId);

    //Check if user already has profile
    boolean existsByUserId(UUID userId);
}
