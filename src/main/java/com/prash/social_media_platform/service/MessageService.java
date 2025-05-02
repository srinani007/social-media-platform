
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

    public List<ConversationDto> listConversations(String me) {
        List<Message> all = repo.findBySenderUsernameOrRecipientUsernameOrderBySentAtDesc(me, me);

        Map<String,ConversationDto> map = new LinkedHashMap<>();
        for (Message m : all) {
            User partner = m.getSender().getUsername().equals(me)
                    ? m.getRecipient()
                    : m.getSender();
            String key = partner.getUsername();

            if (!map.containsKey(key)) {
                String snippet = m.getContent();
                if (snippet.length() > 50) snippet = snippet.substring(0,50) + "…";
                LocalDateTime lastTime = LocalDateTime.ofInstant(
                        m.getSentAt(), ZoneId.systemDefault());

                long unread = repo.countBySenderUsernameAndRecipientUsernameAndReadFalse(
                        partner.getUsername(), me);

                // Mark as read if we're the sender or if there are no unread messages
                boolean isRead = m.getSender().getUsername().equals(me) || (unread == 0);

                map.put(key, new ConversationDto(
                        key,
                        partner.getFullName(),
                        partner.getProfilePictureUrl(),
                        lastTime,
                        snippet,
                        unread,
                        isRead
                ));
            }
        }
        return new ArrayList<>(map.values());
    }


    /** Full one-on-one thread sorted oldest → newest */
    public List<Message> getThread(String me, String other) {
        return repo.findChatThread(me, other);
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

    /**
     * Get new messages in a conversation since the last seen message ID
     */
    public List<Message> getNewMessages(String currentUser, String otherUser, Long lastMessageId) {
        if (lastMessageId == null || lastMessageId == 0) {
            // If no last message ID provided, return an empty list
            return Collections.emptyList();
        }

        // Verify the last message exists and belongs to this conversation
        Optional<Message> lastMessage = repo.findById(lastMessageId);
        if (lastMessage.isEmpty() ||
                !(lastMessage.get().getSender().getUsername().equals(currentUser) ||
                        !(lastMessage.get().getRecipient().getUsername().equals(otherUser)) &&
                                !(lastMessage.get().getSender().getUsername().equals(otherUser) ||
                                        !(lastMessage.get().getRecipient().getUsername().equals(currentUser))))) {
            throw new IllegalArgumentException("Invalid last message ID for this conversation");
        }

        // Get messages sent after the last message and in this conversation
        return repo.findNewMessagesInConversation(
                currentUser,
                otherUser,
                lastMessage.get().getSentAt()
        );
    }

    /**
     * Mark a message as delivered (but not necessarily read)
     */
    @Transactional
    public void markDelivered(Long messageId) {
        repo.findById(messageId).ifPresent(m -> {

            m.setDeliveredAt(new Date());
            repo.save(m);
        });
    }

    /**
     * Delete a message (soft delete for sender, hard delete if both parties delete)
     */
    @Transactional
    public void deleteMessage(Long messageId, String username) {
        repo.findById(messageId).ifPresent(m -> {
            if (m.getSender().getUsername().equals(username)) {
                m.setDeletedBySender(true);
            } else if (m.getRecipient().getUsername().equals(username)) {
                m.setDeletedByRecipient(true);
            } else {
                throw new SecurityException("User not authorized to delete this message");
            }

            // If both parties have deleted, actually remove from a database
            if (m.isDeletedBySender() && m.isDeletedByRecipient()) {
                repo.delete(m);
            } else {
                repo.save(m);
            }
        });
    }

    /**
     * Add or update a reaction to a message
     */
    @Transactional
    public void addReaction(Long messageId, String username, String emoji) {
        repo.findById(messageId).ifPresent(m -> {
            // Get an existing reactions map or create a new one
            Map<String, Set<String>> reactions = m.getReactions();
            if (reactions == null) {
                reactions = new HashMap<>();
            }

            // Remove any existing reaction from this user
            reactions.values().forEach(set -> set.remove(username));

            // Add the new reaction
            reactions.computeIfAbsent(emoji, k -> new HashSet<>()).add(username);

            m.setReactions(reactions);
            repo.save(m);
        });
    }

    /**
     * Mark all messages from a specific user as read
     */
    @Transactional
    public void markConversationAsRead(String me, String other) {
        repo.markConversationAsRead(me, other);
    }

}
