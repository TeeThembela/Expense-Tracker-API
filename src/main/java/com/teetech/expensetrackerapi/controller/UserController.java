package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.dto.UserUpdateDTO;
import com.teetech.expensetrackerapi.dto.UserUpdateResponseDTO;
import com.teetech.expensetrackerapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@PreAuthorize("hasAuthority('ROLE_USER')")
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/{userId}")
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<UserUpdateResponseDTO> updateUser(
            @Valid @RequestBody UserUpdateDTO request,
            @PathVariable UUID userId){
        UserUpdateResponseDTO response = userService.updateUser(request, userId);

        return ResponseEntity.ok(response);
    }
}
