package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.model.Notification;
import com.prash.social_media_platform.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService svc;

    public NotificationController(NotificationService svc) {
        this.svc = svc;
    }

    // Navbar fragment: last 10 notifications + unread count
    @GetMapping("/fragment")
    public String fragment(Model model, Authentication auth) {
        String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : null;
        if (username != null) {
            model.addAttribute("notifications", svc.recent(username));
            model.addAttribute("unreadCount", svc.unreadCount(username));
        } else {
            model.addAttribute("notifications", List.of());
            model.addAttribute("unreadCount", 0);
        }
        return "fragments/navbar :: notifications";
    }

    // Full notifications page: show all and clear unread
    @GetMapping
    public String viewAll(Model model, Authentication auth) {
        String username = auth.getName();
        List<Notification> allNotifications = svc.findAll(username);
        model.addAttribute("allNotifications", allNotifications);
        // mark all as read
        svc.markAllRead(username);
        // reset badge count for the view
        model.addAttribute("unreadCount", 0);
        return "notifications";
    }

    // Mark all as read and redirect to full page
    @PostMapping("/read-all")
    public String markAllRead(Authentication auth) {
        svc.markAllRead(auth.getName());
        return "redirect:/notifications";
    }
}