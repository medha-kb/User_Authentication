package com.intuit.api.controllers;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.intuit.api.dto.ApiResponse;
import com.intuit.api.dto.SignUpRequest;
import com.intuit.services.authentication.signup.SignUpService;
import com.intuit.services.authentication.signup.SignUpValidation;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class SignUpController {

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private SignUpValidation signUpValidation;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        signUpValidation.validate(signUpRequest);
        ApiResponse response = signUpService.registerUser(signUpRequest);
        System.err.println("Printing the response" + response);
        return ResponseEntity.ok(response);
    }

}