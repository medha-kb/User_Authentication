package com.intuit.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.intuit.api.dto.ApiResponse;
import com.intuit.api.dto.JwtResponse;
import com.intuit.api.dto.SignUpRequest;
import com.intuit.api.dto.ViewUserResponse;
import com.intuit.exceptions.UserNotFoundException;
import com.intuit.model.entity.User;
import com.intuit.services.admin.AdminService;
import com.intuit.services.admin.DeleteUserValidation;
import com.intuit.services.authentication.signup.SignUpValidation;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AdminController {

    private final AdminService adminService;
    private final SignUpValidation signUpValidation;
    private final DeleteUserValidation deleteUserValidation;

    @Autowired
    public AdminController(AdminService adminService,
            SignUpValidation signUpValidation,
            DeleteUserValidation deleteUserValidation) {
        this.adminService = adminService;
        this.signUpValidation = signUpValidation;
        this.deleteUserValidation = deleteUserValidation;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-user")
    public ResponseEntity<ApiResponse> addUser(@RequestBody SignUpRequest signUpRequest) {
        signUpValidation.validate(signUpRequest);
        ApiResponse message = adminService.registerUser(signUpRequest);
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        deleteUserValidation.validate(userId);
        ApiResponse message = adminService.deleteUser(userId);
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/view-user/{userId}")
    public ResponseEntity<?> viewUser(@PathVariable Long userId) {
        Optional<User> optionaluser = adminService.viewUser(userId);

        if (optionaluser.isPresent()) {
            User user = optionaluser.get();
            ViewUserResponse viewUserResponse = new ViewUserResponse(user.getId(), user.getEmail(), user.getUsername(),
                    user.getUserRoles());
            return new ResponseEntity<>(viewUserResponse, HttpStatus.OK);
        }
        // return new ResponseEntity<>(new UserNotFoundException("User details not
        // found"), HttpStatus.NOT_FOUND);
        // }
        return new ResponseEntity<>(new UserNotFoundException("User details not found"), HttpStatus.NOT_FOUND);
        // return new ResponseEntity<>(viewUserResponse, HttpStatus.OK); // This line
        // was missing.
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/view-users")
    public ResponseEntity<List<User>> viewUsers() {
        List<User> users = adminService.getAllUsers();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
