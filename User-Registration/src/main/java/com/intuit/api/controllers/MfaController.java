package com.intuit.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.services.authentication.signin.MfaService;

@RestController
@RequestMapping("/auth")
public class MfaController {

    @Autowired
    private MfaService mfaService;

    // @GetMapping("/generateToken")
    // public ResponseEntity<String> generateMfaToken() {
    // String token = mfaService.generateMfaToken();
    // return ResponseEntity.ok(token);
    // }

    @PostMapping("/validateMfaToken")
    public ResponseEntity<?> validateMfaToken(@RequestParam String username, @RequestParam String token) {
        if (mfaService.validateMfaToken(username, token)) {
            return new ResponseEntity<>("Token validated", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid token", HttpStatus.BAD_REQUEST);
        }
    }
}
