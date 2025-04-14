package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.model.Post;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.PostService;
import com.prash.social_media_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        model.addAttribute("posts", postService.findAllPosts());
        return "dashboard";
    }

    @PostMapping("/posts")
    public String createPost(@RequestParam("content") String content, Authentication auth) {
        postService.createPost(content, auth.getName());
        return "redirect:/user/dashboard";
    }
}
