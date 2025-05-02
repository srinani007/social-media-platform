package com.prash.social_media_platform.repository;

import com.prash.social_media_platform.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
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

    // count unread from partner → me
    long countBySenderUsernameAndRecipientUsernameAndReadFalse(
            String senderUsername,
            String recipientUsername
    );

    // Mark all unread as read
    @Modifying
    @Query("UPDATE Message m SET m.read = true WHERE m.recipient.username = :username AND m.read = false")
    void markAllRead(@Param("username") String username);

    // Fetch only new messages in a conversation since a given time
    @Query("""
      SELECT m
        FROM Message m
       WHERE ((m.sender.username = :user1 AND m.recipient.username = :user2)
           OR  (m.sender.username = :user2 AND m.recipient.username = :user1))
         AND m.sentAt > :since
       ORDER BY m.sentAt ASC
    """)
    List<Message> findNewMessagesInConversation(
            @Param("user1") String user1,
            @Param("user2") String user2,
            @Param("since") Instant since
    );

    // Mark one conversation as read (otherUser → currentUser)
    @Modifying
    @Query("""
      UPDATE Message m
         SET m.read = true
       WHERE m.recipient.username = :currentUser
         AND m.sender.username = :otherUser
         AND m.read = false
    """)
    int markConversationAsRead(
            @Param("currentUser") String currentUser,
            @Param("otherUser")   String otherUser
    );


    // Fetch the full chat thread between two users
    @Query("""
      SELECT m
        FROM Message m
        JOIN FETCH m.sender s
        JOIN FETCH m.recipient r
       WHERE (s.username = :me AND r.username = :other)
          OR (s.username = :other AND r.username = :me)
       ORDER BY m.sentAt
    """)
    List<Message> findChatThread(
            @Param("me")    String me,
            @Param("other") String other
    );

}
