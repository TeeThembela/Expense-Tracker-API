package com.teetech.expensetrackerapi.repository;

import com.teetech.expensetrackerapi.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findBySessionId(UUID sessionId);

    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.user.email = :username")
    void deleteAllByUsername(@Param("username") String username);

    Optional<RefreshToken> findByToken(String refreshToken);
}
