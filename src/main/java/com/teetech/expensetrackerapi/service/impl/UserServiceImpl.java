package com.teetech.expensetrackerapi.service.impl;

import com.teetech.expensetrackerapi.dto.UserRequestDTO;
import com.teetech.expensetrackerapi.dto.UserResponseDTO;
import com.teetech.expensetrackerapi.dto.UserUpdateDTO;
import com.teetech.expensetrackerapi.entity.User;
import com.teetech.expensetrackerapi.exception.DuplicateUserException;
import com.teetech.expensetrackerapi.exception.UserNotFoundException;
import com.teetech.expensetrackerapi.mapper.UserMapper;
import com.teetech.expensetrackerapi.repository.UserRepository;
import com.teetech.expensetrackerapi.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper mapper;
    private final UserRepository repository;

    @Transactional
    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {
        log.info("Creating user with email: {}", dto.email());

        // Map to entity
        User user = mapper.toUser(dto);

        // Validate business logic
        if (repository.existsByEmail(user.getEmail())){
            log.warn("Duplicate user creation attempt for email: {}", dto.email());
            throw new DuplicateUserException(user.getEmail());
        }

        // Persist entity
        User savedUser = repository.saveAndFlush(user);
        log.info("User created successfully: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        // Map to DTO and return
        return mapper.toUserDTO(savedUser);
    }

    @Transactional
    @Override
    public UserResponseDTO updateUser(UserUpdateDTO dto, UUID userId) {
        // TODO: Implement when auth layer is ready (email changes tied to re-verification)
        log.warn("updateUser called but not yet implemented: userId={}", userId);
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDTO getUser(String email) {
        // TODO: Implement — will be used by auth layer during login
        log.warn("getUser called but not yet implemented: email={}", email);
        return null;
    }

    // Deletes the user and everything linked to them via CascadeType.ALL:
    // expenses, categories, budgets, and user profile are all removed.
    @Transactional
    @Override
    public void deleteUser(UUID userId) {
        log.info("Deleting user account: userId={}", userId);

        User user = findUserOrThrow(userId);
        repository.delete(user);

        log.info("User account and all associated data deleted: userId={}", userId);
    }



    //Helper methods
        /**
         * Fetches a user by userId.
         * Throws UserNotFoundException if no user exists for that user.
         */
        @Transactional(readOnly = true)
        @Override
        public User findUserOrThrow(UUID userId) {
            log.debug("Fetching User account by Id: {}", userId);
            return repository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("User not found: userId={}", userId);
                        return new UserNotFoundException(userId.toString());
                    });
        }
}