package com.intuit.userservice;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.intuit.api.dto.ApiResponse;
import com.intuit.api.dto.EditProfileRequest;
import com.intuit.api.dto.JwtResponse;
import com.intuit.api.dto.SignInRequest;
import com.intuit.api.dto.SignUpRequest;
import com.intuit.exceptions.UserRegistrationException;
import com.intuit.model.entity.Role;
import com.intuit.model.entity.URole;
import com.intuit.model.entity.User;
import com.intuit.model.entity.UserDetails;
import com.intuit.model.repository.IRoleRepository;
import com.intuit.model.repository.IUserRepository;
import com.intuit.services.utils.JwtTokenMethods;

@Service
public class UserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenMethods jwtTokenMethods;

    public ApiResponse registerUser(SignUpRequest signUpRequest) {

        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = determineRoles(signUpRequest);

        user.setUserRoles(roles);
        userRepository.save(user);

        return new ApiResponse("User registered successfully!");
    }

    private Set<Role> determineRoles(SignUpRequest signUpRequest) {
        Set<Role> roles = new HashSet<>();
        Set<String> rolesFromRequest = signUpRequest.getRole() != null ? signUpRequest.getRole()
                : Collections.emptySet();

        rolesFromRequest.forEach(role -> {
            switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(URole.ROLE_ADMIN)
                            .orElseThrow(() -> new UserRegistrationException("Error: Role not found."));
                    roles.add(adminRole);
                    break;
                case "mod":
                    Role modRole = roleRepository.findByName(URole.ROLE_MODERATOR)
                            .orElseThrow(() -> new UserRegistrationException("Error: Role not found."));
                    roles.add(modRole);
                    break;
                default:
                    Role userRole = roleRepository.findByName(URole.ROLE_USER)
                            .orElseThrow(() -> new UserRegistrationException("Error: Role not found."));
                    roles.add(userRole);
            }
        });
        if (rolesFromRequest.isEmpty()) {
            Role defaultRole = roleRepository.findByName(URole.ROLE_USER)
                    .orElseThrow(() -> new UserRegistrationException("Error: Role not found."));
            roles.add(defaultRole);
        }
        return roles;
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Optionally, if you want to get a user by their username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public JwtResponse authenticateUser(SignInRequest signinRequest) {

        System.out.println("inside authentication");
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signinRequest.getUsername(), signinRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String jwtToken = jwtTokenMethods.generateJWTToken(authenticate);

        UserDetailsImpl userDetails = (UserDetailsImpl) authenticate.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new JwtResponse(userDetails.getId(), jwtToken, userDetails.getUsername(), userDetails.getEmail(), roles);
    }

    public void editUserProfile(EditProfileRequest request) {
        // Fetch existing user

        User user = userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update the basic fields
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Check if userDetails exist, if not create a new one
        UserDetails userDetails = user.getUserDetails();
        if (userDetails == null) {
            userDetails = new UserDetails();
            userDetails.setUser(user);
            user.setUserDetails(userDetails);
        }

        // Update or set the new fields
        userDetails.setPhoneNumber(request.getPhoneNumber());
        userDetails.setCountry(request.getCountry());
        userDetails.setState(request.getState());
        userDetails.setCity(request.getCity());

        userRepository.save(user); // This will also save the user details because of the cascade settings
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}
