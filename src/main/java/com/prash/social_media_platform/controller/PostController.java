package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.model.Post;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.PostService;
import com.prash.social_media_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;


    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication auth) {
        User user = userService.getByUsername(auth.getName());
        model.addAttribute("user", user);
        model.addAttribute("posts", postService.getPostsByUser(user));
        model.addAttribute("post", new Post()); // 👈 this line is essential
        return "dashboard";
    }


    @PostMapping("/posts")
    public String createPost(@ModelAttribute Post post, Authentication auth, RedirectAttributes redirectAttributes) {
        try {
            postService.createPost(post, auth.getName());
            redirectAttributes.addFlashAttribute("success", "Post published successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong 😢");
        }
        return "redirect:/user/dashboard";
    }


}
