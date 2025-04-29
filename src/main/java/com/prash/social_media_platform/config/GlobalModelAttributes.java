package com.prash.social_media_platform.config;

import com.prash.social_media_platform.model.Notification;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.MessageService;
import com.prash.social_media_platform.service.NotificationService;
import com.prash.social_media_platform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

/**
 * Adds common navbar attributes (message count, notifications, URI) to every model.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    private final MessageService messageService;
    private final NotificationService notificationService;
    private final UserService userService;

    public GlobalModelAttributes(MessageService messageService,
                                 NotificationService notificationService,
                                 UserService userService) {
        this.messageService = messageService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("unreadCount")
    public long unreadCount(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return 0L;
        }
        String username = resolveUsername(auth);
        return messageService.countUnreadMessages(username);
    }

    @ModelAttribute("notifications")
    public List<Notification> notifications(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            String username = resolveUsername(auth);
            return notificationService.recent(username);
        }
        return Collections.emptyList();
    }

    @ModelAttribute("notificationCount")
    public int notificationCount(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            String username = resolveUsername(auth);
            return notificationService.unreadCount(username);
        }
        return 0;
    }

    /**
     * Extracts the application's username from Spring Security's Authentication,
     * handling both local and OAuth2 logins.
     */
    private String resolveUsername(Authentication auth) {
        if (auth instanceof OAuth2AuthenticationToken oauth) {
            Object dbUsername = oauth.getPrincipal()
                    .getAttributes()
                    .get("db_username");
            if (dbUsername instanceof String) {
                return (String) dbUsername;
            }
        }
        // Fallback: look up by principal name (email or username)
        User user = userService.getByUsernameOrEmail(auth.getName());
        return user.getUsername();
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error";
    }
}
