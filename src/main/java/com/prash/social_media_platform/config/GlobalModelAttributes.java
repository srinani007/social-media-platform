package com.prash.social_media_platform.config;

import com.prash.social_media_platform.model.Notification;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.MessageService;
import com.prash.social_media_platform.service.NotificationService;
import com.prash.social_media_platform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@Component
@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private final MessageService messageService;
    @Autowired
    private final NotificationService notificationService;

    @Autowired
    private final UserService userService;

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    public GlobalModelAttributes(MessageService messageService,
                                 NotificationService notificationService, UserService userService) {
        this.messageService = messageService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @ModelAttribute("unreadCount")
    public long unreadCount(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return 0;
        }
        // e.g. countUnreadMessages(username) returns a long
        return messageService.countUnreadMessages(auth.getName());
    }

    @ModelAttribute("notifications")
    public List<Notification> notifications(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            User me = userService.getByUsernameOrEmail(auth.getName());
            // use the 'recent' method and pass username
            return notificationService.recent(me.getUsername());
        }
        return Collections.emptyList();
    }


    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error";
    }
}

