package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.model.Comment;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private InteractionService interactions;

    private String extractIdentifier(Authentication auth) {
        if (auth.getPrincipal() instanceof OAuth2User oAuth2) {
            return oAuth2.getAttribute("email");
        }
        return auth.getName();
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication auth) {
        // 1️⃣ extract identifier & lookup
        String identifier = extractIdentifier(auth);
        User user = userService.getByUsernameOrEmail(identifier);


        // 2️⃣ fetch posts & populate model
        List<Post> posts = postService.findAllPosts();
        Map<Long, Long> likeCounts    = posts.stream()
                .collect(Collectors.toMap(Post::getId, p -> interactions.getLikeCount(p.getId())));
        Map<Long, Long> commentCounts = posts.stream()
                .collect(Collectors.toMap(Post::getId, p -> interactions.getCommentCount(p.getId())));
        Map<Long, Long> shareCounts   = posts.stream()
                .collect(Collectors.toMap(Post::getId, p -> interactions.getShareCount(p.getId())));
        Map<Long, Long> repostCounts  = posts.stream()
                .collect(Collectors.toMap(Post::getId, p -> interactions.getRepostCount(p.getId())));

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("post", new Post());
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("commentCounts", commentCounts);
        model.addAttribute("shareCounts", shareCounts);
        model.addAttribute("repostCounts", repostCounts);

        // for comments display:
        Map<Long, List<Comment>> commentsByPost = posts.stream()
                .collect(Collectors.toMap(Post::getId,
                        p-> interactions.getComments(p.getId())));
        model.addAttribute("commentsByPost", commentsByPost);



        // 3️⃣ Pro-only stats
        if (user.isPro()) {
            model.addAttribute("totalPosts", posts.size());

            Map<String, Integer> weeklyCounts = new LinkedHashMap<>();
            for (int i = 6; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                String label = date.getDayOfWeek()
                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                int count = (int) posts.stream()
                        .filter(p -> p.getCreatedAt()
                                .toLocalDate()
                                .equals(date))
                        .count();
                weeklyCounts.put(label, count);
            }
            model.addAttribute("chartLabels", weeklyCounts.keySet());
            model.addAttribute("chartData", weeklyCounts.values());
            model.addAttribute("followersCount", 14 + new Random().nextInt(50));

            Post mostLiked = posts.stream()
                    .max(Comparator.comparing(Post::getCreatedAt))
                    .orElse(null);
            model.addAttribute("mostLikedPostTitle",
                    mostLiked != null ? mostLiked.getTitle() : "N/A");
            model.addAttribute("topPosts", posts.stream().limit(3).toList());
        }

        return "dashboard";
    }

    @PostMapping("/posts")
    public String createPost(@ModelAttribute Post post,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {
        try {
            // 1️⃣ extract identifier & lookup
            String identifier = extractIdentifier(auth);
            User user = userService.getByUsernameOrEmail(identifier);

            // 2️⃣ use the real username for postService
            postService.createPost(post, user.getUsername());
            redirectAttributes.addFlashAttribute("success",
                    "Post published successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Something went wrong 😢");
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
