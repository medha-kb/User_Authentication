package com.intuit.services.admin;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.swing.text.html.Option;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.intuit.api.dto.ApiResponse;
import com.intuit.api.dto.JwtResponse;
import com.intuit.api.dto.SignUpRequest;
import com.intuit.exceptions.UserRegistrationException;
import com.intuit.model.entity.Role;
import com.intuit.model.entity.URole;
import com.intuit.model.entity.User;
import com.intuit.model.repository.IRoleRepository;
import com.intuit.model.repository.IUserRepository;

@Service
public class AdminService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private IRoleRepository roleRepository;

    public ApiResponse deleteUser(Long userId) {

        userRepository.deleteById(userId);
        return new ApiResponse("User Deleted successfully");

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

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

    public Optional<User> viewUser(Long userId) {

        if (userRepository.existsById(userId))
            return userRepository.findById(userId);

        return null;
    }

}
