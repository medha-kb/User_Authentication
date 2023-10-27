package com.intuit.services.password;

import java.util.Locale;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.intuit.model.entity.User;
import com.intuit.services.utils.MfaToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Service
public class EmailService {

    @Autowired
    private MessageSource messages;

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String supportemail;

    private final SimpleMailMessage email = new SimpleMailMessage();

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendPasswordResetEmail(final String contextPath, final String emailmessage, final String token,
            final User user) {
        final String url = contextPath + "changePassword?id=" + user.getId() + "&token=" + token;
        Locale currentLocale = LocaleContextHolder.getLocale();
        final String message = messages.getMessage(emailmessage, null, currentLocale);

        email.setTo(user.getEmail());
        email.setSubject("Reset Password");
        email.setText(message + " \r\n" + url);
        email.setFrom(supportemail);
        javaMailSender.send(email);
    }

    public void sendEmail(MfaToken tokenDetails) {

        email.setTo(tokenDetails.getUseremail());
        email.setSubject(" MFA Token");
        email.setText("MFA token for user authentication" + tokenDetails.getToken());
        email.setFrom(supportemail);
        javaMailSender.send(email);
    }

    // public void sendEmail(String to, String subject, String content) throws
    // MessagingException {
    // MimeMessage message = javaMailSender.createMimeMessage();
    // MimeMessageHelper helper = new MimeMessageHelper(message, true);

    // helper.setTo(to);
    // helper.setSubject(subject);
    // helper.setText(content, true);

    // javaMailSender.send(message);
    // }
}
