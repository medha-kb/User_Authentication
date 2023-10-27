package com.intuit.api.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.intuit.api.dto.ApiResponse;
import com.intuit.model.entity.User;
import com.intuit.services.password.ForgotPasswordService;
import com.intuit.services.password.ForgotPasswordValidation;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService passwordResetService;

    @Autowired
    private ForgotPasswordValidation forgotPasswordValidation;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody Map<String, String> request) {
        String email = request.get("email");
        forgotPasswordValidation.validate(email);
        try {
            passwordResetService.sendPasswordResetEmail(email);
            return ResponseEntity.ok(new ApiResponse("Password reset email sent successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("Failed to send password reset email."));
        }
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> showPasswordResetForm(@RequestParam("token") String token) {

        User user = passwordResetService.findUserByResetToken(token);
        if (user != null) {
            return ResponseEntity.ok("HTML content of the reset password form");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
            @RequestParam("password") String newPassword) {
        User user = passwordResetService.findUserByResetToken(token);
        if (user != null) {
            passwordResetService.resetPassword(user, newPassword);
            return ResponseEntity.ok("Password reset successful");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token");
        }
    }

}
