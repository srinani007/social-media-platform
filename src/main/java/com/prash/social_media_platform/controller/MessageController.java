package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.dto.ConversationDto;
import com.prash.social_media_platform.dto.TypingNotification;
import com.prash.social_media_platform.model.Message;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.MessageService;
import com.prash.social_media_platform.service.NotificationService;
import com.prash.social_media_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MessageController(MessageService messageService,
                             UserService userService,
                             NotificationService notificationService,
                             SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public String listConversations(Model model, Principal principal) {
        String me = principal.getName();
        userService.getByUsernameOrEmail(me);

        List<ConversationDto> conversations = messageService.listConversations(me);
        List<User> allUsers = userService.findAllExcept(me);
        long unreadNotifications = notificationService.unreadCount(me);

        model.addAttribute("selectedUsername", null);
        model.addAttribute("firstUsername", !conversations.isEmpty() ? conversations.get(0).getUsername() : null);
        model.addAttribute("messages", Collections.emptyList());
        model.addAttribute("otherUser", null);
        model.addAttribute("conversations", conversations);
        model.addAttribute("allUsers", userService.findAllExcept(me));
        model.addAttribute("unreadNotifications", unreadNotifications);

        return "message";
    }

    @GetMapping("/{username}")
    public String showChat(@PathVariable("username") String username,
                           Model model,
                           Principal principal) {
        String me = principal.getName();
        userService.getByUsernameOrEmail(me);

        User other = userService.getByUsernameOrEmail(username);
        if (other == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        messageService.markConversationAsRead(me, username);

        List<ConversationDto> conversations = messageService.listConversations(me);
        List<Message> messages = messageService.getThread(me, username);
        List<User> allUsers = userService.findAllExcept(me);
        long unreadNotifications = notificationService.unreadCount(me);

        model.addAttribute("conversations", conversations);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("selectedUsername", username);
        model.addAttribute("firstUsername", conversations.isEmpty() ? null : conversations.get(0).getUsername());
        model.addAttribute("messages", messages);
        model.addAttribute("otherUser", other);
        model.addAttribute("unreadNotifications", unreadNotifications);

        return "message";
    }

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestParam("recipient") String recipientUsername,
                                     @RequestParam("content") String content,
                                     Principal principal) {
        User sender = userService.getByUsernameOrEmail(principal.getName());
        User recipient = userService.getByUsernameOrEmail(recipientUsername);
        if (sender == null || recipient == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sender or recipient not found");
        }


        Message message = messageService.send(sender, recipient, content);

        String chatUrl = "/messages/" + URLEncoder.encode(sender.getUsername(), StandardCharsets.UTF_8);
        notificationService.createNotification(recipient, chatUrl, "New message from " + sender.getFullName());
        System.out.println("Sending message from " + sender.getUsername() + " to " + recipient.getUsername());

        messagingTemplate.convertAndSendToUser(
                recipient.getUsername(),
                "/queue/messages",
                message

        );


        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/read")
    public String markRead(@PathVariable("id") Long messageId,
                           @RequestParam("user") String otherUsername) {
        messageService.markRead(messageId);
        return "redirect:/messages/" + otherUsername;
    }

    @GetMapping("/{username}/updates")
    @ResponseBody
    public List<Message> getUpdates(@PathVariable("username") String otherUsername,
                                    @RequestParam(value = "lastId", required = false) Long lastId,
                                    Principal principal) {
        System.out.println("GET updates: me=" + principal.getName() +
                ", other=" + otherUsername + ", lastId=" + lastId);

        if (lastId == null || lastId == 0) {
            return messageService.getThread(principal.getName(), otherUsername);
        }

        return messageService.getNewMessages(principal.getName(), otherUsername, lastId);
    }
    @MessageMapping("/typing")
    public void handleTypingNotification(@Payload TypingNotification notification, Principal principal) {
        notification.setSender(principal.getName());
        messagingTemplate.convertAndSendToUser(
                notification.getRecipient(),
                "/queue/typing",
                notification
        );
    }



    @PostMapping("/{id}/delivered")
    public ResponseEntity<Void> markDelivered(@PathVariable("id") Long messageId) {
        messageService.markDelivered(messageId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("id") Long messageId,
                                              Principal principal) {
        messageService.deleteMessage(messageId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/react")
    public ResponseEntity<Void> reactToMessage(@PathVariable("id") Long messageId,
                                               @RequestParam("emoji") String emoji,
                                               Principal principal) {
        messageService.addReaction(messageId, principal.getName(), emoji);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/react")
    public ResponseEntity<Void> removeReaction(@PathVariable("id") Long messageId,
                                               @RequestParam("emoji") String emoji,
                                               Principal principal) {
        messageService.removeReaction(messageId, principal.getName(), emoji);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{username}/read")
    public ResponseEntity<Void> markAllRead(@PathVariable("username") String otherUsername,
                                            Principal principal) {
        messageService.markAllRead(principal.getName(), otherUsername);
        return ResponseEntity.ok().build();
    }

}
