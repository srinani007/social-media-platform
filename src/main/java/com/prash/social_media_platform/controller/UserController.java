package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication auth) {
        User user = auth.getPrincipal() instanceof OAuth2User oAuth2User
                ? userService.getByEmail(oAuth2User.getAttribute("email"))
                : userService.getByUsername(auth.getName());

        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") User updatedUser, Authentication auth) {
        User existingUser = auth.getPrincipal() instanceof OAuth2User oAuth2User
                ? userService.getByEmail(oAuth2User.getAttribute("email"))
                : userService.getByUsername(auth.getName());

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        userService.updateUser(existingUser);

        return "redirect:/user/profile?updated=true";
    }
}
