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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

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
        List<Post> posts = postService.findAllPosts();

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("post", new Post());

        // 🔐 Pro-only stats
        if (user.isPro()) {
            // total post count
            model.addAttribute("totalPosts", posts.size());

            // posts this week (grouped by day)
            Map<String, Integer> weeklyCounts = new LinkedHashMap<>();
            for (int i = 6; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                String label = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                int count = (int) posts.stream()
                        .filter(p -> p.getCreatedAt().toLocalDate().equals(date))
                        .count();
                weeklyCounts.put(label, count);
            }

            model.addAttribute("chartLabels", weeklyCounts.keySet());
            model.addAttribute("chartData", weeklyCounts.values());

            // mock followers count
            model.addAttribute("followersCount", 14 + new Random().nextInt(50));

            // most liked post (placeholder logic)
            Post mostLiked = posts.stream().max(Comparator.comparing(Post::getCreatedAt)).orElse(null);
            model.addAttribute("mostLikedPostTitle", mostLiked != null ? mostLiked.getTitle() : "N/A");

            // top 3 posts (for leaderboard, mocked by createdAt desc)
            model.addAttribute("topPosts", posts.stream().limit(3).toList());
        }

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

    @GetMapping("/redirect-dashboard")
    public String redirectToDashboard(Authentication auth) {
        String role = auth.getAuthorities().toString();
        if (role.contains("ADMIN")) {
            return "redirect:/admin";
        } else {
            return "redirect:/user/dashboard";
        }
    }
}
