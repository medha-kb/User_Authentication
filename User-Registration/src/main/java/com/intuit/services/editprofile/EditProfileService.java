package com.intuit.services.editprofile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.intuit.api.dto.EditProfileRequest;
import com.intuit.exceptions.CustomValidationException;
import com.intuit.model.entity.User;
import com.intuit.model.entity.UserDetails;
import com.intuit.model.repository.IUserRepository;
import com.intuit.userservice.UserDetailsImpl;

@Service
public class EditProfileService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    EditProfileValidation editProfileValidation;

    public void editUserDetails(EditProfileRequest request) {

        User user = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new CustomValidationException("User not found"));

        if (editProfileValidation.isValidString.test(request.getUsername())) {
            user.setUsername(request.getUsername());
        } else {
            throw new CustomValidationException("Invalid username provided");
        }

        if (editProfileValidation.isValidString.test(request.getEmail())) {
            user.setEmail(request.getEmail());
        } else {
            throw new CustomValidationException("Invalid email provided");
        }

        UserDetails userDetails = user.getUserDetails();
        if (userDetails == null) {
            userDetails = new UserDetails();
            userDetails.setUser(user);
            user.setUserDetails(userDetails);
        }

        if (editProfileValidation.isValidPhoneNumber.test(request.getPhoneNumber())) {
            userDetails.setPhoneNumber(request.getPhoneNumber());
        } else {
            throw new IllegalArgumentException("Invalid phone number provided");
        }

        userRepository.save(user);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}
