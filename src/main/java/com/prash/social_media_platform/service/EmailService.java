package com.prash.social_media_platform.service;

import com.prash.social_media_platform.model.Message;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendContactEmail(Message message, String username) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo("prasanthkunchanapalli.ac@gmail.com"); // 🔁 Replace with your real email
        email.setSubject("New Contact Message: " + message.getSubject());
        email.setText("From: " + message.getName() + "\nEmail: " + message.getEmail() + "\n\n" + message.getContent());
        mailSender.send(email);
    }

    @PostConstruct
    public void debugEmailConfig() {
        System.out.println(">>> GMAIL_USERNAME = " + System.getProperty("GMAIL_USERNAME"));
        System.out.println(">>> GMAIL_PASSWORD = " + System.getProperty("GMAIL_PASSWORD"));
    }

}
