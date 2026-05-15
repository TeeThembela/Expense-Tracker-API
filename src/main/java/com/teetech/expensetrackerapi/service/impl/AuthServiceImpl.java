package com.teetech.expensetrackerapi.service.impl;

import com.teetech.expensetrackerapi.dto.LoginRequestDTO;
import com.teetech.expensetrackerapi.dto.RegisterRequestDTO;
import com.teetech.expensetrackerapi.dto.TokenResult;
import com.teetech.expensetrackerapi.entity.Authority;
import com.teetech.expensetrackerapi.entity.RefreshToken;
import com.teetech.expensetrackerapi.entity.User;
import com.teetech.expensetrackerapi.exception.EmailAlreadyExistsException;
import com.teetech.expensetrackerapi.exception.InvalidTokenException;
import com.teetech.expensetrackerapi.repository.AuthorityRepository;
import com.teetech.expensetrackerapi.repository.TokenRepository;
import com.teetech.expensetrackerapi.repository.UserRepository;
import com.teetech.expensetrackerapi.security.model.UserPrincipal;
import com.teetech.expensetrackerapi.service.AuthService;
import com.teetech.expensetrackerapi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Transactional
    @Override
    public void register(RegisterRequestDTO request) {

        if (userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException(request.email());
        }

        Authority authority = authorityRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_USER not found — seed authorities first"));

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .authorities(new HashSet<>(Set.of(authority)))
                .build();

        userRepository.save(user);
    }

    @Override
    public Map<String, Object> login(LoginRequestDTO request) {

         Authentication authentication = authenticationManager.authenticate(
                 new UsernamePasswordAuthenticationToken(
                         request.email(),
                         request.password()
                 ));

        return buildTokenResponse((UserPrincipal) authentication.getPrincipal());
    }

    @Transactional
    @Override
    public Map<String, Object> refresh(String refreshToken) {

        if (!jwtUtil.isRefreshTokenValid(refreshToken)){
            throw new InvalidTokenException("Invalid refresh token");
        }

        RefreshToken stored = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not recognised"));

        tokenRepository.delete(stored);

        String username = jwtUtil.extractUsername(refreshToken);

        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(username);

        return buildTokenResponse(principal);

    }

    @Transactional
    @Override
    public void logout(String sessionId) {
      RefreshToken token = tokenRepository.findBySessionId(UUID.fromString(sessionId))
              .orElseThrow(() -> new InvalidTokenException("Refresh token not recognised"));

        tokenRepository.delete(token);

    }

    @Transactional
    @Override
    public void logoutAll(String username) {
        try {
            tokenRepository.deleteAllByUsername(username);
        } catch (Exception e) {
            throw new InvalidTokenException("No refresh token found for this user");
        }
    }

    @Transactional
    protected Map<String, Object> buildTokenResponse(UserPrincipal principal){

            UUID sessionId = UUID.randomUUID();

            TokenResult accessResult = jwtUtil.generateAccessToken(principal, sessionId);
            TokenResult refreshResult = jwtUtil.generateRefreshToken(principal, sessionId);

            tokenRepository.save(RefreshToken.builder()
                    .token(refreshResult.token())
                    .user(principal.getUser())
                    .sessionId(sessionId)
                    .expiresAt(refreshResult.expiresAt())
                    .build());

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("accessToken", accessResult.token());
            result.put("accessTokenExpiration", accessResult.expiresAt());
            result.put("refreshToken", refreshResult.token());

            return result;
    }
}
