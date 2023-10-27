package com.intuit.services.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intuit.exceptions.CustomValidationException;
import com.intuit.model.repository.IUserRepository;

@Service
public class DeleteUserValidation {

    @Autowired
    IUserRepository userRepository;

    public void validate(Long userid) {

        if (!userRepository.existsById(userid)) {
            throw new CustomValidationException(" User id does not exists");
        }
    }
}
