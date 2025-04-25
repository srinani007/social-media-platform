package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.service.NotificationService;
import com.prash.social_media_platform.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class NotificationController {

    @Autowired
    private final NotificationService svc;

    public NotificationController(NotificationService svc) {
        this.svc = svc;
    }

    @GetMapping("/notifications")
    public String list(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("notifications", svc.recent(user.getUsername()));
        model.addAttribute("unreadCount", svc.unreadCount(user.getUsername()));
        return "fragments/navbar :: notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllRead(@AuthenticationPrincipal User user) {
        svc.markAllRead(user.getUsername());
        return "redirect:/notifications";
    }
}
