package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.service.PostService;
import com.prash.social_media_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("posts", postService.findAllPosts());
        return "admin";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/upgrade/{id}")
    public String upgradeToPro(@PathVariable Long id) {
        userService.setProStatus(id, true);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/admin/dashboard";
    }
    @PostMapping("/promote/{id}")
    public String promoteToAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.setRole(id, "ROLE_ADMIN");
            redirectAttributes.addFlashAttribute("success", "User promoted to Admin!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Promotion failed.");
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping
    public String redirectToAdminDashboard() {
        return "redirect:/admin/dashboard";
    }


}
