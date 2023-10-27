package com.intuit.services.password;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intuit.exceptions.CustomValidationException;
import com.intuit.model.repository.IUserRepository;

@Service
public class ForgotPasswordValidation {

    @Autowired
    private IUserRepository userRepository;

    public void validate(String email) {

        if (!userRepository.existsByEmail(email)) {
            throw new CustomValidationException("Invalid user email");
        }

    }

}
