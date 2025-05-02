package com.prash.social_media_platform.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Entity @Table(name = "messages")
@NoArgsConstructor @AllArgsConstructor @Builder
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreationTimestamp
    private Instant sentAt;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    private String name;
    private String email;

    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean delivered;
    private Date deliveredAt;

    @Transient
    private boolean deletedBySender;
    @Transient
    private boolean deletedByRecipient;

    @ElementCollection
    @CollectionTable(name = "message_reactions",
            joinColumns = @JoinColumn(name = "message_id"))
    @MapKeyColumn(name = "emoji")
    @Column(name = "user_id")
    private Map<String, Set<String>> reactions;

    public Map<String, Set<String>> getReactions() {
        return reactions;
    }

    public void setReactions(Map<String, Set<String>> reactions) {
        this.reactions = reactions;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public Date getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Date deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public boolean isDeletedBySender() {
        return deletedBySender;
    }

    public void setDeletedBySender(boolean deletedBySender) {
        this.deletedBySender = deletedBySender;
    }

    public boolean isDeletedByRecipient() {
        return deletedByRecipient;
    }

    public void setDeletedByRecipient(boolean deletedByRecipient) {
        this.deletedByRecipient = deletedByRecipient;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
