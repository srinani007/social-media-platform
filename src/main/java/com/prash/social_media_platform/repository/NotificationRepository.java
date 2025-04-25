// com/prash/social_media_platform/repository/NotificationRepository.java
package com.prash.social_media_platform.repository;

import com.prash.social_media_platform.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserUsernameOrderByTimestampDesc(String username);
    int countByUserUsernameAndReadFalse(String username);
}
