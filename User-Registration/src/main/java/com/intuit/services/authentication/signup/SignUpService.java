package com.intuit.services.authentication.signup;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.intuit.api.dto.ApiResponse;
import com.intuit.api.dto.SignUpRequest;
import com.intuit.exceptions.UserRegistrationException;
import com.intuit.model.entity.Role;
import com.intuit.model.entity.URole;
import com.intuit.model.entity.User;
import com.intuit.model.repository.IRoleRepository;
import com.intuit.model.repository.IUserRepository;

@Service
public class SignUpService {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    public ApiResponse registerUser(SignUpRequest signUpRequest) {

        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = addingRoles(signUpRequest);

        user.setUserRoles(roles);
        userRepository.save(user);

        return new ApiResponse("User registered successfully!");
    }

    private Set<Role> addingRoles(SignUpRequest signUpRequest) {

        Set<String> rolesFromRequest = signUpRequest.getRole() != null ? signUpRequest.getRole()
                : Collections.emptySet();

        // If roles are not present in the request, defaulting to ROLE_USER
        if (rolesFromRequest.isEmpty()) {
            rolesFromRequest = Collections.singleton("user");
        }

        return rolesFromRequest.stream()
                .map(role -> {
                    switch (role) {
                        case "admin":
                            return roleRepository.findByName(URole.ROLE_ADMIN)
                                    .orElseThrow(() -> new UserRegistrationException("Error: Role not found."));
                        default:
                            return roleRepository.findByName(URole.ROLE_USER)
                                    .orElseThrow(() -> new UserRegistrationException("Error: Role not found."));
                    }
                })
                .collect(Collectors.toSet());
    }

}
