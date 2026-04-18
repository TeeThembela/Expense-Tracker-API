package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.dto.UserProfileRequestDTO;
import com.teetech.expensetrackerapi.dto.UserProfileResponseDTO;
import com.teetech.expensetrackerapi.dto.UserProfileUpdateDTO;
import com.teetech.expensetrackerapi.service.UserProfileService;
import com.teetech.expensetrackerapi.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users/{userId}/profile")
public class UserProfileController {
    private final UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<UserProfileResponseDTO> createUserProfile(
            @Valid
            @RequestBody UserProfileRequestDTO dto,
            @PathVariable UUID userId
            ){
        UserProfileResponseDTO userProfileResponseDTO = userProfileService.createUserProfile(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userProfileResponseDTO);
    }

    @GetMapping
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@PathVariable UUID userId){
        UserProfileResponseDTO userProfileResponseDTO = userProfileService.getUserProfile(userId);
        return ResponseEntity.ok(userProfileResponseDTO);
    }

    @PutMapping
    public ResponseEntity<UserProfileResponseDTO> updateUserProfile(
            @Valid
            @RequestBody UserProfileUpdateDTO dto,
            @PathVariable UUID userId){
        UserProfileResponseDTO userProfileResponseDTO = userProfileService.updateUserProfile(dto, userId);
        return ResponseEntity.ok(userProfileResponseDTO);
    }

}
