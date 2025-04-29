package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    private final UserService userService;

    @Autowired
    public RedirectController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/redirect-dashboard")
    public String redirectToDashboard(Authentication auth) {
        // auth.getName() is the DB username (thanks to nameAttributeKey="db_username")
        String username = auth.getName();
        User user = userService.getByUsernameOrEmail(username);

        if (user == null) {
            // Handle the case where the user is not found
            throw new IllegalArgumentException("User not found for username: " + username);
        }
        // Admins go to admin dashboard
        if (user.getRole().contains("ADMIN")) {
            return "redirect:/admin/dashboard";
        }

        // Regular users: choose pro vs free dashboard
        return user.isPro()
                ? "redirect:/user/dashboard?pro=true"
                : "redirect:/user/dashboard?pro=false";
    }
    @GetMapping("/dashboard")
    public String dashboardAlias() {
        // forward or redirect to your real dashboard URL
        return "redirect:/user/dashboard";
    }
}
