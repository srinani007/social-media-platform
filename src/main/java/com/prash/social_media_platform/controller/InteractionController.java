package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.service.InteractionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/posts")
public class InteractionController {

    @Autowired private InteractionService interactions;
    @Autowired private HttpServletRequest request;

    private String getRedirectBack() {
        String referer = request.getHeader("Referer");
        return (referer != null) ? "redirect:" + referer : "redirect:/user/dashboard";
    }

    @PostMapping("/{postId}/like")
    public String like(@PathVariable Long postId, Authentication auth) {
        interactions.likePost(postId, auth.getName());
        return getRedirectBack();
    }

    @PostMapping("/{postId}/comment")
    public String comment(@PathVariable Long postId,
                          @RequestParam String content,
                          Authentication auth) {
        interactions.commentOnPost(postId, content, auth.getName());
        return getRedirectBack();
    }

    @PostMapping("/{postId}/share")
    public String share(@PathVariable Long postId, Authentication auth) {
        interactions.sharePost(postId, auth.getName());
        return getRedirectBack();
    }

    @PostMapping("/{postId}/repost")
    public String repost(@PathVariable Long postId,
                         Authentication auth) {
        interactions.repostPost(postId, auth.getName());
        return "redirect:/user/dashboard";
    }
}

