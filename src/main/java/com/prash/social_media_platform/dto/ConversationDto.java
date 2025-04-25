package com.prash.social_media_platform.dto;

import java.time.LocalDateTime;

public class ConversationDto {
    private final String username;
    private final String displayName;
    private final String avatarUrl;
    private final LocalDateTime lastMessageTime;
    private final String lastMessageSnippet;

    public ConversationDto(String username,
                           String displayName,
                           String avatarUrl,
                           LocalDateTime lastMessageTime,
                           String lastMessageSnippet) {
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.lastMessageTime = lastMessageTime;
        this.lastMessageSnippet = lastMessageSnippet;
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
