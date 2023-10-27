package com.intuit.services.password;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.context.i18n.LocaleContextHolder;

import com.intuit.exceptions.CustomValidationException;
import com.intuit.model.entity.PasswordResetToken;
import com.intuit.model.entity.User;
import com.intuit.model.repository.IPasswordResetTokenRepository;
import com.intuit.model.repository.IUserRepository;

@Service
public class ForgotPasswordService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IPasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private MessageSource messages;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String supportemail;

    public ForgotPasswordService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendPasswordResetEmail(String email) {

        Supplier<String> tokenSupplier = () -> UUID.randomUUID().toString();
        String resetToken = tokenSupplier.get();
        System.out.println("inside the service class" + email);
        try {
            System.out.println("Fetching the user from thedb");
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomValidationException("User not found with email: " + email));

            System.out.println("user found" + user.getUsername());
            PasswordResetToken passwordResetToken = new PasswordResetToken();

            passwordResetToken.setToken(resetToken);
            passwordResetToken.setUser(user);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, 24);
            passwordResetToken.setExpiryDate(calendar.getTime());

            sendPasswordResetEmail(passwordResetToken, user);

            // Saving the token for verification on reset request
            passwordResetTokenRepository.save(passwordResetToken);
        } catch (Exception e) {
            System.out.println("Error searching for user by email: " + e.getMessage());
            throw e;
        }
    }

    private void sendPasswordResetEmail(final PasswordResetToken passwordResetToken, final User user) {

        final String url = "http://localhost:8080/reset-password?token=" + passwordResetToken.getToken();
        System.out.println("password link " + url);
        Locale currentLocale = LocaleContextHolder.getLocale();
        final String message = messages.getMessage("message.resetPassword", null, currentLocale);
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Reset Password");
        email.setText(message + " \r\n" + url);
        email.setFrom(supportemail);
        javaMailSender.send(email);
    }

    private String generateResetToken() {
        // Generate a unique reset token
        return UUID.randomUUID().toString();
    }

    public User findUserByResetToken(String resetToken) {

        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByToken(resetToken);
        if (optionalToken.isPresent()) {
            PasswordResetToken passwordResetToken = optionalToken.get();
            if (!isTokenExpired(passwordResetToken)) {
                User user = passwordResetToken.getUser();
                return user;
            } else {
                System.out.println("Token Expired");
            }
        } else {
            System.out.println("Token not found");
        }

        return null;
    }

    private boolean isTokenExpired(PasswordResetToken token) {
        Date now = new Date();
        Date tokenExpiry = token.getExpiryDate();
        return now.after(tokenExpiry);
    }

    public void resetPassword(User user, String newPassword) {

        System.out.println("Reseting the password");
        String hashedPassword = passwordEncoder.encode(newPassword);
        try {
            user.setPassword(hashedPassword);
            userRepository.save(user);
            System.out.println("Password rest done, have to delete token");
            passwordResetTokenRepository.deleteByUserId(user.getId());
        } catch (Exception e) {
            System.out.println("Error deleting email: " + e.getMessage());
            throw e;
        }
    }

}
