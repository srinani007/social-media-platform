package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.model.Post;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.InteractionService;
import com.prash.social_media_platform.service.PostService;
import com.prash.social_media_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private PostService postService;
    @Autowired private InteractionService interactionService;

    @GetMapping("/profile")
    public String showProfile(Model model, Authentication auth) {
        // 1️⃣ Lookup current user
        User user = (auth.getPrincipal() instanceof OAuth2User oauth)
                ? userService.getByEmail(oauth.getAttribute("email"))
                : userService.getByUsername(auth.getName());

        // 2️⃣ Fetch *only* this user's posts
        List<Post> posts = postService.getPostsByUser(user);

        // 3️⃣ (Optional) Build like/comment/share/repost counts & comments map
        Map<Long, Long> likeCounts    = posts.stream()
                .collect(Collectors.toMap(Post::getId, p -> interactionService.getLikeCount(p.getId())));
        Map<Long, Long> commentCounts = posts.stream()
                .collect(Collectors.toMap(Post::getId, p -> interactionService.getCommentCount(p.getId())));
        Map<Long, Long> shareCounts   = posts.stream()
                .collect(Collectors.toMap(Post::getId, p -> interactionService.getShareCount(p.getId())));
        Map<Long, Long> repostCounts  = posts.stream()
                .collect(Collectors.toMap(Post::getId, p -> interactionService.getRepostCount(p.getId())));
        Map<Long, java.util.List<com.prash.social_media_platform.model.Comment>> commentsByPost =
                posts.stream().collect(Collectors.toMap(
                        Post::getId,
                        p -> interactionService.getComments(p.getId())
                ));

        // 4️⃣ Add everything to the model
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("commentCounts", commentCounts);
        model.addAttribute("shareCounts", shareCounts);
        model.addAttribute("repostCounts", repostCounts);
        model.addAttribute("commentsByPost", commentsByPost);

        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") User updatedUser, Authentication auth) {
        User existingUser = (auth.getPrincipal() instanceof OAuth2User oauth)
                ? userService.getByEmail(oauth.getAttribute("email"))
                : userService.getByUsername(auth.getName());

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        userService.updateUser(existingUser);

        return "redirect:/user/profile?success";
    }
}
