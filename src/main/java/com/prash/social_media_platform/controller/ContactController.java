package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.model.Message;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.EmailService;
import com.prash.social_media_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ContactController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @GetMapping("/contact")
    public String showContactForm(Model model) {
        model.addAttribute("message", new Message());
        return "contact"; // Maps to contact.html
    }

    @PostMapping("/contact")
    public String sendMessage(@ModelAttribute("message") Message message, Authentication auth, Model model) {
        User user = userService.getByUsername(auth.getName());
        emailService.sendContactEmail(message, user.getUsername());
        model.addAttribute("success", "Message sent successfully!");
        return "contact"; // Redirect to the same page to show a success message
    }
}