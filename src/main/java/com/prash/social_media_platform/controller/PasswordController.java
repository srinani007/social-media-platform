package com.prash.social_media_platform.controller;


import com.prash.social_media_platform.service.PasswordResetService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordController {
    private final PasswordResetService resetService;

    public PasswordController(PasswordResetService resetService) {
        this.resetService = resetService;
    }

    // Show “Forgot Password” form
    @GetMapping("/forgot-password")
    public String showForgot() {
        return "forgot-password";   // create this Thymeleaf template
    }

    // Handle its submission
    @PostMapping("/forgot-password")
    public String handleForgot(@RequestParam String email,
                               HttpServletRequest request,
                               Model model) {
        String appUrl = request.getRequestURL()
                .toString()
                .replace(request.getRequestURI(), "");
        try {
            resetService.requestPasswordReset(email, appUrl);
            model.addAttribute("message",
                    "If an account exists for that email, you’ll receive a reset link shortly.");
        } catch (UsernameNotFoundException | MessagingException e) {
            // we don’t reveal which emails exist or not; show same message
            model.addAttribute("message",
                    "If an account exists for that email, you’ll receive a reset link shortly.");
        }
        return "forgot-password";   // reuse the form template
    }


    // Show the “New Password” form
    @GetMapping("/reset-password")
    public String showReset(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";      // create this template
    }

    // Process new password
    @PostMapping("/reset-password")
    public String handleReset(@RequestParam String token,
                              @RequestParam String password,
                              Model model) {
        try {
            resetService.resetPassword(token, password);
            return "redirect:/login?resetSuccess";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            return "reset-password";
        }
    }
}

