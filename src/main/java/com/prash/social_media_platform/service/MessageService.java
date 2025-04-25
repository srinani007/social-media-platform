package com.prash.social_media_platform.service;

import com.prash.social_media_platform.dto.ConversationDto;
import com.prash.social_media_platform.model.Message;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository repo;

    public MessageService(MessageRepository repo) {
        this.repo = repo;
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

        LinkedHashMap<String, ConversationDto> map = new LinkedHashMap<>();
        for (Message m : all) {
            // determine the other party
            User partner = m.getSender().getUsername().equals(me)
                    ? m.getRecipient()
                    : m.getSender();
            String key = partner.getUsername();
            if (!map.containsKey(key)) {
                // snippet logic unchanged
                String snippet = m.getContent();
                if (snippet.length() > 50) {
                    snippet = snippet.substring(0, 50) + "...";
                }

                // convert Instant to LocalDateTime here:
                LocalDateTime lastTime = LocalDateTime.ofInstant(
                        m.getSentAt(),                // your Instant timestamp
                        ZoneId.systemDefault()        // server’s default zone
                );

                map.put(key, new ConversationDto(
                        key,
                        partner.getFullName(),
                        partner.getProfilePictureUrl(),
                        lastTime,    // now a LocalDateTime
                        snippet
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
}
