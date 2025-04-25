package com.prash.social_media_platform.repository;

import com.prash.social_media_platform.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // for your "inbox" (not used in the fancy UI, but still available)
    List<Message> findByRecipientUsernameOrderBySentAtDesc(String username);
    int countByRecipientUsernameAndReadFalse(String username);

    // 1) Find all messages where I am sender OR recipient, newest first:
    List<Message> findBySenderUsernameOrRecipientUsernameOrderBySentAtDesc(
            String senderUsername,
            String recipientUsername
    );

    // 2) Fetch a two-way thread between me and them, oldest → newest:
    @Query("""
      SELECT m
      FROM Message m
      WHERE  (m.sender.username   = :me AND m.recipient.username = :them)
         OR  (m.sender.username   = :them AND m.recipient.username = :me)
      ORDER BY m.sentAt ASC
    """)
    List<Message> findChatThread(
            @Param("me")   String me,
            @Param("them") String them
    );
}
