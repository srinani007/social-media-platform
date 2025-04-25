// com/prash/social_media_platform/service/NotificationService.java
package com.prash.social_media_platform.service;

import com.prash.social_media_platform.model.Notification;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.repository.NotificationRepository;

import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class NotificationService {
    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }


    public Notification notify(User user, String title, String message, String link) {
          Notification n = new Notification();
          n.setUser(user);
          n.setTitle(title);
          n.setMessage(message);
          n.setLink(link);
          n.setRead(false);
          // timestamp is auto-set by @CreationTimestamp (or DB default)
          return repo.save(n);
      }

    public Notification createNotification(User user, String link, String message) {
        return notify(
                user,
                "Message Received",
                message,
                link
        );
    }

    public List<Notification> recent(String username) {
        return repo.findByUserUsernameOrderByTimestampDesc(username).stream()
                .limit(10)
                .toList();
    }

    public int unreadCount(String username) {
        return repo.countByUserUsernameAndReadFalse(username);
    }

    public void markAllRead(String username) {
        repo.findByUserUsernameOrderByTimestampDesc(username)
                .forEach(n -> {
                    n.setRead(true);
                    repo.save(n);
                });
      }
}

