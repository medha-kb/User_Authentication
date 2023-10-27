package com.intuit.services.authentication.signin;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.intuit.api.dto.SignInRequest;
import com.intuit.exceptions.CustomValidationException;
import com.intuit.model.entity.User;
import com.intuit.model.repository.IUserRepository;

@Service
public class SignInValidation {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void validate(SignInRequest request) {

        if (request == null) {
            throw new CustomValidationException("Username and password cannot be null.");
        }

        if (request.getUsername() == null) {
            throw new CustomValidationException("Username cannot be null.");
        }

        if (request.getPassword() == null) {
            throw new CustomValidationException("Password cannot be null.");
        }

        Optional<User> user = userRepository.findByUsername(request.getUsername());
        if (!user.isPresent()) {
            System.out.println("Username");
            throw new CustomValidationException("Username not found.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            throw new CustomValidationException("Incorrect password.");
        }
    }
}
