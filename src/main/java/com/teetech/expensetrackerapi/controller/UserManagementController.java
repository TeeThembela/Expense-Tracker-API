package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasAuthority('ROLE_MANAGER')")
public class UserManagementController {

    private final UserService userService;

    @PatchMapping("/{userId}/disable")
    public ResponseEntity<Void> disableAccount(@PathVariable UUID userId) {
        userService.disableAccount(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/enable")
    public ResponseEntity<Void> enableAccount(@PathVariable UUID userId) {
        userService.endableAccount(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}