package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.dto.ConversationDto;
import com.prash.social_media_platform.model.Message;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.MessageService;
import com.prash.social_media_platform.service.NotificationService;
import com.prash.social_media_platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService    userService;
    private final NotificationService notificationService;

    public MessageController(MessageService messageService, UserService userService, NotificationService notificationService) {
        this.messageService = messageService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String inbox(@RequestParam(value = "user", required = false)
                        String selectedUsername,
                        Model model,
                        Principal principal) {
        String me = principal.getName();

        List<ConversationDto> conversations =
            messageService.listConversations(me);

        // 2) Decide which user to show
        User selectedUser;
        if (selectedUsername != null) {
            selectedUser = userService.getByUsernameOrEmail(selectedUsername);
        } else if (!conversations.isEmpty()) {
            selectedUser = userService
                .getByUsernameOrEmail(conversations.get(0).getUsername());
        } else {
            // no conversations yet → default to yourself
            selectedUser = userService.getByUsernameOrEmail(me);
        }

        // 3) Fetch the full thread
        List<Message> messages =
            messageService.getThread(me, selectedUser.getUsername());

        List<User> allUsers = userService.findAllExcept(me);

        model.addAttribute("conversations", conversations);
        model.addAttribute("selectedUser",   selectedUser);
        model.addAttribute("messages",       messages);
        model.addAttribute("allUsers", allUsers);
        return "message";
    }

    @PostMapping("/send")
    public String send(@RequestParam("recipient") String recipientUsername,
                       @RequestParam("content")   String content,
                       Principal principal) {
        User sender = userService.getByUsernameOrEmail(principal.getName());
        User recipient = userService.getByUsernameOrEmail(recipientUsername);
        Message m = messageService.send(sender, recipient, null, content);

        // 2) Create a notification for the recipient
        notificationService.createNotification(
                recipient,
                "/messages?user=" + sender.getUsername(),             // link back to this convo
                "New message from " + sender.getFullName()         // display text
        );

        return "redirect:/messages?user=" + URLEncoder
                .encode(recipientUsername, StandardCharsets.UTF_8);
    }

    @PostMapping("/{id}/read")
    public String markRead(@PathVariable("id") Long messageId,
                           @RequestParam("user") String selectedUsername) {
        messageService.markRead(messageId);
        return "redirect:/messages?user=" + selectedUsername;
    }

}
