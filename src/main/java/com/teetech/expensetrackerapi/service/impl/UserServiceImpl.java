package com.teetech.expensetrackerapi.service.impl;

import com.teetech.expensetrackerapi.dto.RegisterRequestDTO;
import com.teetech.expensetrackerapi.dto.UserResponseDTO;
import com.teetech.expensetrackerapi.dto.UserUpdateDTO;
import com.teetech.expensetrackerapi.dto.UserUpdateResponseDTO;
import com.teetech.expensetrackerapi.entity.User;
import com.teetech.expensetrackerapi.exception.DuplicateUserException;
import com.teetech.expensetrackerapi.exception.UserNotFoundException;
import com.teetech.expensetrackerapi.mapper.UserMapper;
import com.teetech.expensetrackerapi.repository.UserRepository;
import com.teetech.expensetrackerapi.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper mapper;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    // Deletes the user and everything linked to them via CascadeType.ALL:
    // expenses, categories, budgets, and user profile are all removed.
    @Transactional
    @Override
    public void deleteUser(UUID userId) {
        log.debug("Deleting user account: userId={}", userId);

        User user = findUserOrThrow(userId);
        repository.delete(user);

        log.debug("User account and all associated data deleted: userId={}", userId);
    }

    @Override
    public void disableAccount(UUID userId) {

        log.debug("Disabling user account: userId={}", userId);
        disableOrEnableAccount(userId, false, false);
    }

    @Override
    public void endableAccount(UUID userId) {
        log.debug("Enabling user account: userId={}", userId);
        disableOrEnableAccount(userId, true, true);
    }

    @Transactional
    @Override
    public UserUpdateResponseDTO updateUser(UserUpdateDTO request, UUID userId) {
        log.debug("Updating user account: userId={}", userId);

        String email = request.email();
        String password = request.password();

        if (email.isBlank() && password.isBlank()){
            log.warn("No field to provided for the update");
            throw new IllegalArgumentException("Provide at least a new email or password to update the user.");
        }
        if (email.isBlank()){
            email = null;
        }

        User user = findUserOrThrow(userId);

        String responseMessage = null;
        if (email != null && password != null){
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            responseMessage = "Successfully updated the user's email and password";
        } else if (email != null) {
            user.setEmail(email);
            responseMessage = "Successfully updated the user's email";
        } else {
            user.setPassword(passwordEncoder.encode(password));
            responseMessage = "Successfully updated the user's password";
        }

        repository.save(user);

        return new UserUpdateResponseDTO(responseMessage);
    }

    protected void disableOrEnableAccount(UUID userId, boolean accountNonLocked, boolean enabled) {
        User user = findUserOrThrow(userId);

        user.setAccountNonLocked(accountNonLocked);
        user.setEnabled(enabled);

        repository.save(user);
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