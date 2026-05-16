package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.dto.ErrorResponseDTO;
import com.teetech.expensetrackerapi.dto.UserProfileRequestDTO;
import com.teetech.expensetrackerapi.dto.UserProfileResponseDTO;
import com.teetech.expensetrackerapi.dto.UserProfileUpdateDTO;
import com.teetech.expensetrackerapi.security.model.UserPrincipal;
import com.teetech.expensetrackerapi.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User Profile", description = "Create and manage the personal profile attached to a user account")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_USER')")
@RequestMapping("/api/v1/users/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(
            summary = "Create a user profile",
            description = """
                    Creates the personal profile (first name, last name, phone number, display name)
                    for the currently authenticated user. The user ID is resolved from the JWT — no
                    path parameter is needed.

                    If `displayName` is omitted, the service falls back to the user's first name.

                    A profile can only be created once per account. Calling this endpoint again when a
                    profile already exists will return a 409.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created successfully",
                    content = @Content(schema = @Schema(implementation = UserProfileResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed — required fields missing or phone number not exactly 10 digits",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "A profile already exists for this user account",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<UserProfileResponseDTO> createUserProfile(
            @Valid @RequestBody UserProfileRequestDTO dto,
            @AuthenticationPrincipal UserPrincipal principal) {

        UserProfileResponseDTO response = userProfileService.createUserProfile(dto, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get a user profile by user ID",
            description = "Returns the personal profile for the specified user. " +
                    "Requires the authenticated user to be the owner of the `userId` path parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile found",
                    content = @Content(schema = @Schema(implementation = UserProfileResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner of this userId",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found — user has not created a profile yet",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{userId}")
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(
            @Parameter(description = "UUID of the user whose profile to retrieve", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId) {

        UserProfileResponseDTO response = userProfileService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update a user profile",
            description = "Updates one or more fields of the user's personal profile. " +
                    "All fields are optional — only supplied fields are changed. " +
                    "Requires the authenticated user to be the owner of the `userId` path parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = UserProfileResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed — field exceeds max length or phone number not exactly 10 digits",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner of this userId",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{userId}")
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<UserProfileResponseDTO> updateUserProfile(
            @Valid @RequestBody UserProfileUpdateDTO dto,
            @Parameter(description = "UUID of the user whose profile to update", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId) {

        UserProfileResponseDTO response = userProfileService.updateUserProfile(dto, userId);
        return ResponseEntity.ok(response);
    }
}