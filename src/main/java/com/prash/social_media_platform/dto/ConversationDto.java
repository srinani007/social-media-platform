package com.prash.social_media_platform.dto;

import java.time.LocalDateTime;

public class ConversationDto {
    private final String username;
    private final String displayName;
    private final String avatarUrl;
    private final LocalDateTime lastMessageTime;
    private final String lastMessageSnippet;
    private long   unreadCount;
    private boolean read;

    public ConversationDto(String username,
                           String displayName,
                           String avatarUrl,
                           LocalDateTime lastMessageTime,
                           String lastMessageSnippet,
                           long unreadCount,
                            boolean read) {
        this.username           = username;
        this.displayName        = displayName;
        this.avatarUrl          = avatarUrl;
        this.lastMessageTime    = lastMessageTime;
        this.lastMessageSnippet = lastMessageSnippet;
        this.unreadCount        = unreadCount;
        this.read              = read;
    }


    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public long getUnreadCount() {
        return unreadCount;
    }
    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public String getLastMessageSnippet() {
        return lastMessageSnippet;
    }
}
