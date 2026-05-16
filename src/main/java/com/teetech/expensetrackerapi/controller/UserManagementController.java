package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.dto.ErrorResponseDTO;
import com.teetech.expensetrackerapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "User Management (Admin)", description = "Administrative operations on user accounts. Requires ROLE_MANAGER authority.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasAuthority('ROLE_MANAGER')")
public class UserManagementController {

    private final UserService userService;

    @Operation(
            summary = "Disable a user account",
            description = "Disables the specified user account, preventing the user from authenticating. " +
                    "Existing sessions are not automatically invalidated — use in conjunction with " +
                    "a session revocation mechanism if immediate lockout is required. " +
                    "Requires `ROLE_MANAGER` authority."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account disabled successfully",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — ROLE_MANAGER authority required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PatchMapping("/{userId}/disable")
    public ResponseEntity<Void> disableAccount(
            @Parameter(description = "UUID of the user account to disable", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId) {
        userService.disableAccount(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Enable a previously disabled user account",
            description = "Re-enables a user account that was previously disabled, restoring the ability to authenticate. " +
                    "Requires `ROLE_MANAGER` authority."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account enabled successfully",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — ROLE_MANAGER authority required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PatchMapping("/{userId}/enable")
    public ResponseEntity<Void> enableAccount(
            @Parameter(description = "UUID of the user account to enable", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId) {
        userService.endableAccount(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Permanently delete a user account",
            description = "Deletes a user account and all associated data. This action is irreversible. " +
                    "Requires `ROLE_MANAGER` authority."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User account deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — ROLE_MANAGER authority required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "UUID of the user account to delete", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}