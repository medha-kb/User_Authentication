package com.intuit.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.api.dto.EditProfileRequest;
import com.intuit.exceptions.CustomValidationException;
import com.intuit.services.editprofile.EditProfileService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class EditProfileController {

    @Autowired
    private EditProfileService editProfileService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/edit-profile")
    public ResponseEntity<?> editUserProfile(@RequestBody EditProfileRequest request) {

        try {
            editProfileService.editUserDetails(request);
            return new ResponseEntity<>("Profile updated successfully", HttpStatus.OK);
        } catch (CustomValidationException ex) {
            return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
        } catch (Exception ex) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}