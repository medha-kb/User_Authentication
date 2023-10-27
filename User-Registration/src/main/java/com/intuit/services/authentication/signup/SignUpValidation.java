package com.intuit.services.authentication.signup;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intuit.api.dto.SignUpRequest;
import com.intuit.exceptions.CustomValidationException;
import com.intuit.model.repository.IUserRepository;

@Service
public class SignUpValidation {

    @Autowired
    private IUserRepository userRepository;

    public void validate(SignUpRequest request) {
        final String username = request.getUsername();
        final String email = request.getEmail();
        final String password = request.getPassword();

        // using predicate rules to validate the post api request
        List<ValidationRule> rules = Arrays.asList(
                req -> {
                    if (userRepository.existsByUsername(username)) {
                        throw new CustomValidationException("User with the same username already exists.");
                    }
                },
                req -> {
                    if (userRepository.existsByEmail(email)) {
                        throw new CustomValidationException("Email is already registered.");
                    }
                },
                req -> {
                    if (email == null || email.isEmpty()) {
                        throw new CustomValidationException("Email cannot be empty.");
                    }
                },
                req -> {
                    if (username == null || username.isEmpty()) {
                        throw new CustomValidationException("Username cannot be empty.");
                    }
                },
                req -> {
                    if (password == null || password.isEmpty()) {
                        throw new CustomValidationException("Password cannot be empty.");
                    }
                },
                req -> {
                    List<String> reservedNames = Arrays.asList("admin", "support", "user");
                    if (reservedNames.contains(username.toLowerCase())) {
                        throw new CustomValidationException("This username is reserved and cannot be used.");
                    }
                },
                req -> {
                    if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
                        throw new CustomValidationException("Invalid email format.");
                    }
                },
                req -> {
                    if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
                        throw new CustomValidationException(
                                "Password must include uppercase, lowercase, number, special char, and be at least 8 characters.");
                    }
                },
                req -> {
                    if (password.contains(req.getUsername())) {
                        throw new CustomValidationException("Password should not contain your username.");
                    }
                },
                req -> {
                    String emailPrefix = req.getEmail().split("@")[0];
                    if (password.contains(emailPrefix)) {
                        throw new CustomValidationException("Password should not contain parts of your email.");
                    }
                },
                req -> {
                    if (username.length() > 255 || req.getEmail().length() > 255 || req.getPassword().length() > 255) {
                        throw new CustomValidationException("Field values are too long.");
                    }
                });

        rules.forEach(rule -> rule.validate(request));
    }

    @FunctionalInterface
    interface ValidationRule {
        void validate(SignUpRequest request) throws CustomValidationException;
    }
}
