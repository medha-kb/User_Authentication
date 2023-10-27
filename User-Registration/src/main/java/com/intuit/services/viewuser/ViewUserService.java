package com.intuit.services.viewuser;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.intuit.api.dto.ApiResponse;
import com.intuit.exceptions.UserNotFoundException;
import com.intuit.model.entity.User;
import com.intuit.model.repository.IUserRepository;
import com.intuit.userservice.UserDetailsImpl;

@Service
public class ViewUserService {

    @Autowired
    private IUserRepository userRepository;

    public Optional<User> getUserDetails() {

        // getCurrentUserId().ifPresent(id -> {
        // Optional<User> user = userRepository.findById(id);
        // });

        Long userid = getCurrentUserId();
        // .orElse(null);
        return userRepository.findById(userid);

    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication");
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println("Principal" + userDetails);
        return userDetails.getId();
    }
    // private Long getCurrentUserId() {
    // return
    // Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
    // .map(Authentication::getPrincipal)
    // .filter(UserDetailsImpl.class::isInstance)
    // .map(UserDetailsImpl.class::cast)
    // .map(UserDetailsImpl::getId);
    // }

}
