package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.dto.ErrorResponseDTO;
import com.teetech.expensetrackerapi.dto.UserUpdateDTO;
import com.teetech.expensetrackerapi.dto.UserUpdateResponseDTO;
import com.teetech.expensetrackerapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Users", description = "Update user account credentials (email and password)")
@RestController
@PreAuthorize("hasAuthority('ROLE_USER')")
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Update user account credentials",
            description = "Updates the email address and/or password for the specified account. " +
                    "Both fields are optional — supply only the field(s) you want to change. " +
                    "If the new email is already taken by another account, a 409 is returned. " +
                    "Requires the authenticated user to be the owner of the `userId` path parameter."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credentials updated successfully",
                    content = @Content(schema = @Schema(implementation = UserUpdateResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed — email format invalid or password outside 8–128 character range",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden — authenticated user is not the owner of this userId",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Email address is already taken by another account",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{userId}")
    @PreAuthorize("authentication.principal.id.equals(#userId)")
    public ResponseEntity<UserUpdateResponseDTO> updateUser(
            @Valid @RequestBody UserUpdateDTO request,
            @Parameter(description = "UUID of the user account to update", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", required = true)
            @PathVariable UUID userId) {

        UserUpdateResponseDTO response = userService.updateUser(request, userId);
        return ResponseEntity.ok(response);
    }
}