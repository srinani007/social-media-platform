package com.prash.social_media_platform.config;

import com.prash.social_media_platform.model.Notification;
import com.prash.social_media_platform.service.MessageService;
import com.prash.social_media_platform.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
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

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    public GlobalModelAttributes(MessageService messageService,
                                 NotificationService notificationService) {
        this.messageService = messageService;
        this.notificationService = notificationService;
    }

    @ModelAttribute("unreadCount")
    public int unreadCount(Authentication auth) {
        return (auth != null && auth.isAuthenticated())
                ? messageService.unreadCount(auth.getName())
                : 0;
    }

    @ModelAttribute("notifications")
    public List<Notification> notifications(Authentication auth) {
        return (auth != null && auth.isAuthenticated())
                ? notificationService.recent(auth.getName())
                : Collections.emptyList();
    }
}

