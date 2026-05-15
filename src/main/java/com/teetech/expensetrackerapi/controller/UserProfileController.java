package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.dto.UserProfileRequestDTO;
import com.teetech.expensetrackerapi.dto.UserProfileResponseDTO;
import com.teetech.expensetrackerapi.dto.UserProfileUpdateDTO;
import com.teetech.expensetrackerapi.security.model.UserPrincipal;
import com.teetech.expensetrackerapi.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_USER')")
@RequestMapping("/api/v1/users/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<UserProfileResponseDTO> createUserProfile(
            @Valid
            @RequestBody UserProfileRequestDTO dto,
            @AuthenticationPrincipal UserPrincipal principal){

        UserProfileResponseDTO userProfileResponseDTO = userProfileService
                .createUserProfile(dto, principal.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(userProfileResponseDTO);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@PathVariable UUID userId){

        UserProfileResponseDTO userProfileResponseDTO = userProfileService.getUserProfile(userId);
        return ResponseEntity.ok(userProfileResponseDTO);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<UserProfileResponseDTO> updateUserProfile(
            @Valid
            @RequestBody UserProfileUpdateDTO dto,
            @PathVariable UUID userId){

        UserProfileResponseDTO userProfileResponseDTO = userProfileService.updateUserProfile(dto, userId);
        return ResponseEntity.ok(userProfileResponseDTO);
    }

}
