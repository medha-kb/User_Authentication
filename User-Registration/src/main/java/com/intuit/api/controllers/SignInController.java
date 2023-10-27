package com.intuit.api.controllers;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intuit.api.dto.JwtResponse;
import com.intuit.api.dto.SignInRequest;
import com.intuit.exceptions.CustomValidationException;
import com.intuit.model.entity.User;
import com.intuit.model.repository.IUserRepository;
import com.intuit.services.authentication.signin.MfaService;
import com.intuit.services.authentication.signin.SignInService;
import com.intuit.services.authentication.signin.SignInValidation;
import com.intuit.services.password.EmailService;
import com.intuit.services.utils.MfaToken;
import com.intuit.userservice.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class SignInController {

	@Autowired
	private SignInService signInService;

	@Autowired
	SignInValidation signInValidation;

	// @Autowired
	// private EmailService emailService;

	// @Autowired
	// private CacheManager cacheManager;

	@Autowired
	MfaService mfaService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInRequest signinRequest) {

		signInValidation.validate(signinRequest);
		JwtResponse response = signInService.authenticateUser(signinRequest);

		Function<JwtResponse, ResponseEntity<?>> responseHandler = (resp) -> {
			if (resp != null) {
				return ResponseEntity.ok(resp);
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		};

		return responseHandler.apply(response);

		// if (response != null) {
		// try {
		// CompletableFuture<Boolean> future =
		// mfaService.waitForMfaValidation(signinRequest.getUsername());
		// Boolean isMfaValidated = future.get(2, TimeUnit.MINUTES);
		// if (isMfaValidated) {
		// return new ResponseEntity<>(response, HttpStatus.OK);
		// } else {
		// // If MFA is not validated within the specified time
		// return new ResponseEntity<>("MFA token validation timeout.",
		// HttpStatus.REQUEST_TIMEOUT);
		// }
		// } catch (InterruptedException | ExecutionException | TimeoutException e) {
		// return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		// }
		// }

		// //return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		// return ResponseEntity(response.OK);
	}
}
