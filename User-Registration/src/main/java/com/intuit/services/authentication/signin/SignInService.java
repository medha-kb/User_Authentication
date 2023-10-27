package com.intuit.services.authentication.signin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;

import com.intuit.api.dto.JwtResponse;
import com.intuit.api.dto.SignInRequest;
import com.intuit.services.utils.JwtTokenMethods;
import com.intuit.userservice.UserDetailsImpl;

@Service
public class SignInService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenMethods jwtTokenMethods;

    public JwtResponse authenticateUser(SignInRequest signinRequest) {

        System.out.println("inside authentication");
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signinRequest.getUsername(), signinRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String jwtToken = jwtTokenMethods.generateJWTToken(authenticate);

        UserDetailsImpl userDetails = (UserDetailsImpl) authenticate.getPrincipal();
        // List<String> roles = userDetails.getAuthorities().stream()
        // .map(item -> item.getAuthority())
        // .collect(Collectors.toList());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        System.out.println(jwtToken);
        return new JwtResponse(userDetails.getId(), jwtToken, userDetails.getUsername(), userDetails.getEmail(), roles);
    }
}
