package com.prash.social_media_platform.websocket;

import com.prash.social_media_platform.dto.ChatMessage;
import com.prash.social_media_platform.model.Message;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.MessageService;
import com.prash.social_media_platform.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserService userService;

    public ChatWebSocketController(SimpMessagingTemplate messagingTemplate,
                                   MessageService messageService,
                                   UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.userService = userService;
    }

    @MessageMapping("/ws-chat")
    public void handleChat(@Payload ChatMessage chatMessage) {
        User sender = userService.getByUsernameOrEmail(chatMessage.getSender());
        User recipient = userService.getByUsernameOrEmail(chatMessage.getRecipient());

        // Store in DB
        Message saved = messageService.send(sender, recipient, chatMessage.getContent());

        // Push to recipient
        messagingTemplate.convertAndSendToUser(
                recipient.getUsername(), "/queue/messages", saved
        );
    }
}
