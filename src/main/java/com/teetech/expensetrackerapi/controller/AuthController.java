package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.dto.*;
import com.teetech.expensetrackerapi.security.model.UserPrincipal;
import com.teetech.expensetrackerapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Tag(name = "Authentication", description = "User registration, login, token refresh, and logout operations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Register a new user account",
            description = "Creates a new user account with the supplied email and password. " +
                    "The email address must be unique across all accounts. " +
                    "No authentication is required to call this endpoint.",
            security = {}   // Public — overrides the global JWT requirement
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created successfully",
                    content = @Content(schema = @Schema(implementation = RegisterResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed — email format invalid or password outside length constraints",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Email address is already registered",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponseDTO("Registration successful."));
    }

    @Operation(
            summary = "Log in with email and password",
            description = """
                    Authenticates the user and returns a short-lived **access token** in the response body.
                    A long-lived **refresh token** is simultaneously written as an `HttpOnly; Secure` cookie
                    named `refreshToken`, scoped to the path `/api/v1/auth/refresh`.

                    After a successful login, copy the `accessToken` value and click the **Authorize** button
                    at the top of this page to authenticate subsequent requests.

                    No authentication is required to call this endpoint.
                    """,
            security = {}   // Public
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful — access token returned in body, refresh token set as HttpOnly cookie",
                    content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed — missing or malformed fields",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletResponse response) {

        Map<String, Object> result = authService.login(request);

        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", result.get("refreshToken").toString())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(AuthResponseDTO.builder()
                .accessToken(result.get("accessToken").toString())
                .tokenType("Bearer")
                .expireIn((Instant) result.get("accessTokenExpiration"))
                .build());
    }

    @Operation(
            summary = "Refresh the access token using the refresh token cookie",
            description = """
                    Issues a new access token and rotates the refresh token. The old `refreshToken`
                    cookie is replaced with a fresh one in the same response.

                    **Credential:** The `refreshToken` HttpOnly cookie set by `/login` — not a Bearer token.
                    This endpoint is public in the security filter chain; the cookie is the sole credential.

                    > **Swagger UI limitation:** HttpOnly cookies cannot be inspected or sent manually
                    > from the browser. Use a full HTTP client such as Postman or curl — log in first
                    > to receive the cookie, then call this endpoint in the same session.
                    """,
            security = {}   // Public — cookie is the credential, not a Bearer token
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refreshed — new access token in body, rotated refresh token cookie set",
                    content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Refresh token cookie is absent",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Refresh token is invalid, expired, or has been revoked",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(
            @CookieValue(name = "refreshToken") String refreshToken,
            HttpServletResponse response) {

        Map<String, Object> result = authService.refresh(refreshToken);

        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", result.get("refreshToken").toString())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(AuthResponseDTO.builder()
                .accessToken(result.get("accessToken").toString())
                .tokenType("Bearer")
                .expireIn((Instant) result.get("accessTokenExpiration"))
                .build());
    }

    @Operation(
            summary = "Log out of the current session",
            description = "Invalidates the session identified by the `sessionId` claim in the current JWT, " +
                    "and clears the `refreshToken` cookie. Only the current device/session is affected. " +
                    "Requires a valid JWT access token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Session invalidated and refresh token cookie cleared",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated — valid JWT access token required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletResponse response) {

        if (principal.getSessionId() != null) {
            authService.logout(principal.getSessionId());
        }

        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth/refresh")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Log out of all sessions",
            description = "Invalidates every active session for the authenticated user across all devices. " +
                    "Use this when you suspect an account has been compromised. " +
                    "Requires a valid JWT access token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "All sessions invalidated and refresh token cookie cleared",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authenticated — valid JWT access token required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/logoutAll")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logoutAll(
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletResponse response) {

        authService.logoutAll(principal.getUsername());

        ResponseCookie cookie = ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth/refresh")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }
}