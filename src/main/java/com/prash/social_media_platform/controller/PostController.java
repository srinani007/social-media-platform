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
    public String showDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userService.getByUsername(username);
        List<Post> posts = postService.getPostsByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);

        return "dashboard";
    }


    @PostMapping("/posts")
    public String createPost(@RequestParam("content") String content, Authentication auth) {
        postService.createPost(content, auth.getName());
        return "redirect:/user/dashboard";
    }
}
