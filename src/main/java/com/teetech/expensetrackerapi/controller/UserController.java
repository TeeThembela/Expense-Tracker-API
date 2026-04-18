package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.dto.UserRequestDTO;
import com.teetech.expensetrackerapi.dto.UserResponseDTO;
import com.teetech.expensetrackerapi.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO){
        var userResponseDTO = userService.createUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserProfile(
            @PathVariable UUID userId) {

        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
