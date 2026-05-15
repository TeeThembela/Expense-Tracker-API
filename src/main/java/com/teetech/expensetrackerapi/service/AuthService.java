package com.teetech.expensetrackerapi.service;

import com.teetech.expensetrackerapi.dto.LoginRequestDTO;
import com.teetech.expensetrackerapi.dto.RegisterRequestDTO;

import java.util.Map;

public interface AuthService {
    void register(RegisterRequestDTO request);
    Map<String, Object> login(LoginRequestDTO request);
    Map<String, Object> refresh(String refreshToken);
    void logout(String refreshToken);
    void logoutAll(String username);
}
