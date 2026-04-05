package com.teetech.expensetrackerapi.repository;

import com.teetech.expensetrackerapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    //Find user by email
    Optional<User> findByEmail(String email);

    //Check existence by email
    boolean existsByEmail(String email);
}
