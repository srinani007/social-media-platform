// MessageRepository.java
package com.prash.social_media_platform.repository;

import com.prash.social_media_platform.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Inbox: messages received, newest first
    List<Message> findByRecipientUsernameOrderBySentAtDesc(String username);

    // Unread count
    int countByRecipientUsernameAndReadFalse(String username);

    // All messages where user is sender OR recipient, newest first
    List<Message> findBySenderUsernameOrRecipientUsernameOrderBySentAtDesc(
            String senderUsername,
            String recipientUsername
    );

    // One-on-one thread sorted oldest → newest
    @Query("""
      SELECT m
      FROM Message m
      WHERE  (m.sender.username   = :me AND m.recipient.username = :them)
         OR  (m.sender.username   = :them AND m.recipient.username = :me)
      ORDER BY m.sentAt ASC
    """ )
    List<Message> findChatThread(
            @Param("me")   String me,
            @Param("them") String them
    );

    // Mark all unread as read
    @Modifying
    @Query("UPDATE Message m SET m.read = true WHERE m.recipient.username = :username AND m.read = false")
    void markAllRead(@Param("username") String username);
}