package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "success", required = false) String success,
                            @RequestParam(value = "error", required = false) String error,
                            Model model) {
        if (success != null) model.addAttribute("success", "Registration successful! Please log in.");
        if (error != null) model.addAttribute("error", "Invalid username or password.");
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        // 👑 Auto-assign ADMIN role if username matches "admin"
        if (user.getUsername().equalsIgnoreCase("admin")) {
            user.setRole("ROLE_ADMIN");
        } else {
            user.setRole("ROLE_USER");
        }

        try {
            userService.registerUser(user);
            return "redirect:/login?success";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Username or email already exists.");
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
        }

        return "register";
    }

}
