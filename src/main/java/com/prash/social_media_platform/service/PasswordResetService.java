package com.prash.social_media_platform.service;

import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {
    private final UserRepository users;
    private final JavaMailSender mailSender;
    private final PasswordEncoder encoder;  // you already have one for sign-up

    public PasswordResetService(UserRepository users,
                                JavaMailSender mailSender,
                                PasswordEncoder encoder) {
        this.users = users; this.mailSender = mailSender; this.encoder = encoder;
    }

    /**
     * Generates a token, saves it on the user, and emails the reset link.
     */
    @Transactional
    public void requestPasswordReset(String email, String appUrl) throws MessagingException {
        User user = users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        users.save(user);

        // build the reset link
        String resetUrl = appUrl + "/reset-password?token=" + token;

        // send email
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
        helper.setTo(user.getEmail());
        helper.setSubject("Password Reset Request");
        String html = "<p>Hi " + user.getFullName() + ",</p>"
                + "<p>You requested a password reset. Click <a href=\"" + resetUrl +"\">here</a> to set a new password.</p>"
                + "<p>This link expires in one hour.</p>";
        helper.setText(html, true);
        mailSender.send(msg);
    }


    /**
     * Validates the token, updates the password, and clears the token.
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = users.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        users.save(user);
    }
}
