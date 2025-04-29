package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.model.Notification;
import com.prash.social_media_platform.service.MessageService;
import com.prash.social_media_platform.service.NotificationService;
import com.prash.social_media_platform.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;
    private final MessageService messageService;

    public NotificationController(NotificationService notificationService,
                                  UserService userService, MessageService messageService) {
        this.notificationService = notificationService;
        this.userService         = userService;
        this.messageService = messageService;
    }


    // Navbar fragment: last 10 notifications + unread count
    @GetMapping("/fragment")
    public String fragment(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            String username = userService.getByUsernameOrEmail(auth.getName()).getUsername();

            model.addAttribute("notifications",     notificationService.recent(username));
            model.addAttribute("notificationCount", notificationService.unreadCount(username));
            model.addAttribute("unreadCount",       messageService.unreadCount(username));
        } else {
            model.addAttribute("notifications",     Collections.emptyList());
            model.addAttribute("notificationCount", 0);
            model.addAttribute("unreadCount",       0);
        }

        // Make sure your navbar.html has th:fragment="navbar" on the <nav>…
        return "fragments/navbar :: navbar";
    }

    // Full notifications page: show all and clear unread
    @GetMapping
    public String viewAll(Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        String username = userService.getByUsernameOrEmail(auth.getName()).getUsername();
        notificationService.markAllRead(username);
        List<Notification> allNotifs = notificationService.findAll(username);
        model.addAttribute("notifications",     allNotifs);
        model.addAttribute("notificationCount", 0);
        return "notifications";  // ensure notifications.html exists
    }
    @PostMapping("/mark-read/{id}")
    @ResponseBody
    public ResponseEntity<Void> markRead(@PathVariable Long id,
                                         Authentication auth) {
        // you could optionally check auth and ownership here
        notificationService.markRead(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping(path="/mark-all-read", produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> markAllRead(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            String username = userService
                    .getByUsernameOrEmail(auth.getName())
                    .getUsername();
            notificationService.markAllRead(username);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/clear-all")
    @ResponseBody
    public ResponseEntity<Void> clearAll(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            String username = userService
                    .getByUsernameOrEmail(auth.getName())
                    .getUsername();
            notificationService.clearAll(username);  // you’ll implement this
        }
        return ResponseEntity.noContent().build();
    }

}