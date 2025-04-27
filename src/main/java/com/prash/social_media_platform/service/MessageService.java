
// MessageService.java
package com.prash.social_media_platform.service;

import com.prash.social_media_platform.dto.ConversationDto;
import com.prash.social_media_platform.model.Message;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.repository.MessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class MessageService {
    private final MessageRepository repo;

    public MessageService(MessageRepository repo) {
        this.repo = repo;
    }

    public Message send(User sender, User recipient, String content) {
        return send(sender, recipient, null, content);
    }

    public Message send(User sender, User recipient, String subject, String content) {
        Message m = new Message();
        m.setSender(sender);
        m.setRecipient(recipient);
        m.setSubject(subject);
        m.setContent(content);
        return repo.save(m);
    }

    /** Build the sidebar: one ConversationDto per chat partner, ordered by latest message */
    public List<ConversationDto> listConversations(String me) {
        List<Message> all = repo
                .findBySenderUsernameOrRecipientUsernameOrderBySentAtDesc(me, me);

        Map<String,ConversationDto> map = new LinkedHashMap<>();
        for (Message m : all) {
            User partner = m.getSender().getUsername().equals(me)
                    ? m.getRecipient()
                    : m.getSender();
            String key = partner.getUsername();
            if (!map.containsKey(key)) {
                String snippet = m.getContent();
                if (snippet.length()>50) snippet = snippet.substring(0,50)+"…";
                LocalDateTime lastTime = LocalDateTime.ofInstant(
                        m.getSentAt(), ZoneId.systemDefault());

                // count unread where me is recipient and partner is sender
                long unread = repo.countBySenderUsernameAndRecipientUsernameAndReadFalse(
                        key, me);

                map.put(key,new ConversationDto(
                        key,
                        partner.getFullName(),
                        partner.getProfilePictureUrl(),
                        lastTime,
                        snippet,
                        unread        // ← fill this in
                ));
            }
        }
        return new ArrayList<>(map.values());
    }


    /** Full one-on-one thread sorted oldest → newest */
    public List<Message> getThread(String me, String them) {
        return repo.findChatThread(me, them);
    }

    /** Legacy inbox by recipient only */
    public List<Message> inbox(String username) {
        return repo.findByRecipientUsernameOrderBySentAtDesc(username);
    }

    public int unreadCount(String username) {
        return repo.countByRecipientUsernameAndReadFalse(username);
    }

    public void markRead(Long messageId) {
        repo.findById(messageId).ifPresent(m -> {
            m.setRead(true);
            repo.save(m);
        });
    }

    /** Combined inbox & sent for full message list */
    public List<Message> getMessagesForUser(String username) {
        return repo.findBySenderUsernameOrRecipientUsernameOrderBySentAtDesc(username, username);
    }

    @Transactional
    public void markAllAsRead(String username) {
        repo.markAllRead(username);
    }

    public long countUnreadMessages(String username) {
        return repo.countByRecipientUsernameAndReadFalse(username);
    }
}
