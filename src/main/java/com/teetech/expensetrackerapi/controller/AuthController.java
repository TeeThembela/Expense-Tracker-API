package com.teetech.expensetrackerapi.controller;

import com.teetech.expensetrackerapi.dto.*;
import com.teetech.expensetrackerapi.security.model.UserPrincipal;
import com.teetech.expensetrackerapi.service.AuthService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

   @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request){
       authService.register(request);
       return ResponseEntity.status(HttpStatus.CREATED)
               .body(new RegisterResponseDTO("Registration successful."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response){

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

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(
            @CookieValue(name = "refreshToken") String refreshToken,
            HttpServletResponse response){

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

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout( @AuthenticationPrincipal UserPrincipal principal,
            HttpServletResponse response){

       if (principal.getSessionId() != null){
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

    @PostMapping("/logoutAll")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logoutAll( @AuthenticationPrincipal UserPrincipal principal,
                                        HttpServletResponse response){

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
