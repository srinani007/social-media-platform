// NotificationService.java
package com.prash.social_media_platform.service;

import com.prash.social_media_platform.model.Notification;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    // Build and save a generic notification
    public Notification notify(User user, String title, String message, String link) {
        Notification n = new Notification();
        n.setUser(user);
        n.setTitle(title);
        n.setMessage(message);
        n.setLink(link);
        n.setRead(false);
        return repo.save(n);
    }

    // Convenience for message notifications
    public Notification createNotification(User user, String link, String message) {
        return notify(user, "Message Received", message, link);
    }

    // Last 10 notifications for navbar dropdown
    public List<Notification> recent(String username) {
        return repo.findByUserUsernameOrderByTimestampDesc(username)
                .stream()
                .limit(10)
                .toList();
    }

    // Full list for notifications page
    public List<Notification> findAll(String username) {
        return repo.findByUserUsernameOrderByTimestampDesc(username);
    }

    public int unreadCount(String username) {
        return repo.countByUserUsernameAndReadFalse(username);
    }

    @Transactional
    public void markAllRead(String username) {
        List<Notification> all = repo.findByUserUsernameOrderByTimestampDesc(username);
        for (Notification n : all) {
            if (!n.isRead()) {
                n.setRead(true);
                repo.save(n);
            }
        }
    }
}
