package com.intuit.services.authentication.signin;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.intuit.model.entity.User;
import com.intuit.model.repository.IUserRepository;
import com.intuit.services.password.EmailService;
import com.intuit.services.utils.MfaToken;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;

@Service
public class MfaService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private EmailService emailService;

    @Async
    public CompletableFuture<Boolean> waitForMfaValidation(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            return CompletableFuture.completedFuture(false);
        }

        Supplier<String> tokenSupplier = () -> UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String token = tokenSupplier.get();
        MfaToken tokenDetails = new MfaToken(user.get().getEmail(), token, false);
        emailService.sendEmail(tokenDetails);

        cacheManager.getCache("mfaTokens").put(username, tokenDetails);

        long startTime = System.currentTimeMillis();
        int waitDurationInMillis = 120000; // 2 minutes

        while ((System.currentTimeMillis() - startTime) < waitDurationInMillis) {
            MfaToken storedTokenDetails = (MfaToken) cacheManager.getCache("mfaTokens").get(username).get();

            if (storedTokenDetails.getIsValidated()) {
                return CompletableFuture.completedFuture(true);
            }

            // Sleep for a while before checking again
            try {
                Thread.sleep(5000); // Sleep for 5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return CompletableFuture.completedFuture(false);
            }
        }
        return CompletableFuture.completedFuture(false);
    }

    public boolean validateMfaToken(String username, String token) {
        // Cache mfaTokenCache = cacheManager.getCache("mfaTokens");
        CaffeineCache caffeineCache = (CaffeineCache) cacheManager.getCache("mfaTokens");
        if (caffeineCache != null && caffeineCache.get(username) != null) {
            ValueWrapper valueWrapper = caffeineCache.get(username);
            if (valueWrapper == null) {
                return false;
            }
            MfaToken mfaToken = (MfaToken) valueWrapper.get();

            if (token.equals(mfaToken.getToken())) {
                mfaToken.setIsValidated(true);
                caffeineCache.put(username, mfaToken); // Update the token in cache
                return true;
            }
        }
        return false;
    }
}
