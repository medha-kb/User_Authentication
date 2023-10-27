package com.intuit.api.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.intuit.api.dto.JwtResponse;
import com.intuit.exceptions.UserNotFoundException;
import com.intuit.model.entity.User;
import com.intuit.services.viewuser.ViewUserService;

@RestController
@RequestMapping("/auth")
public class ViewUserController {

    @Autowired
    private ViewUserService viewUserService;

    @GetMapping("/currentuser")
    public ResponseEntity<?> viewUser() {

        Optional<User> user = viewUserService.getUserDetails();
        JwtResponse viewUserResponse = user.map(userobj -> new JwtResponse(userobj.getId(), userobj.getUsername(),
                userobj.getEmail(), userobj.getUserRoles())).orElse(null);

        if (viewUserResponse == null) {
            return new ResponseEntity<>(new UserNotFoundException("User details not found"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(viewUserResponse, HttpStatus.OK);
    }

}
