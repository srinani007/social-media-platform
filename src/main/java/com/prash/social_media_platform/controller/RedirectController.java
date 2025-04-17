package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @Autowired
    private UserService userService;

    @GetMapping("/redirect-dashboard")
    public String redirectToDashboard(Authentication auth) {
        String username = auth.getName();
        String role = auth.getAuthorities().toString();

        if (role.contains("ADMIN")) {
            return "redirect:/admin";
        }

        // For USER: check Pro status
        User user = userService.getByUsername(username);
        if (user.isPro()) {
            return "redirect:/user/dashboard?pro=true";
        } else {
            return "redirect:/user/dashboard?pro=false";
        }
    }
}
