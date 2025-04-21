package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @Autowired
    private UserService userService;

    @GetMapping("/redirect-dashboard")
    public String redirectToDashboard(Authentication auth) {
        String username;

        // OAuth2 login: get email from OAuth2User
        if (auth.getPrincipal() instanceof OAuth2User oAuth2User) {
            username = oAuth2User.getAttribute("email");
        } else {
            // Standard login
            username = auth.getName();
        }

        User user = userService.getByUsername(username);

        if (user.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        }

        return user.isPro()
                ? "redirect:/user/dashboard?pro=true"
                : "redirect:/user/dashboard?pro=false";
    }
}