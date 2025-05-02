package com.prash.social_media_platform.controller;

import com.prash.social_media_platform.dto.ConversationDto;
import com.prash.social_media_platform.model.Message;
import com.prash.social_media_platform.model.User;
import com.prash.social_media_platform.service.MessageService;
import com.prash.social_media_platform.service.NotificationService;
import com.prash.social_media_platform.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final MessageService      messageService;
    private final UserService         userService;
    private final NotificationService notificationService;

    public MessageController(MessageService messageService,
                             UserService userService,
                             NotificationService notificationService) {
        this.messageService      = messageService;
        this.userService         = userService;
        this.notificationService = notificationService;
    }

    /**
     * 1) Inbox / Conversations overview
     *    URL: GET /messages
     */
    @GetMapping
    public String listConversations(Model model, Principal principal) {
        String me = principal.getName();
        userService.getByUsernameOrEmail(me);

        // 1) sidebar summaries
        List<ConversationDto> conversations =
                messageService.listConversations(me);

        // 2) "new chat" dropdown
        List<User> allUsers =
                userService.findAllExcept(me);

        // 3) unread notifications count
        long unreadNotifications =
                notificationService.unreadCount(me);

        // if we already have at least one thread, go straight there
        if (!conversations.isEmpty()) {
            String firstUsername = conversations.get(0).getUsername();
            return "redirect:/messages/" + firstUsername;
        }

        // otherwise render placeholder view
        model.addAttribute("conversations",       conversations);
        model.addAttribute("allUsers",            userService.findAllExcept(me));
        model.addAttribute("unreadNotifications", notificationService.unreadCount(me));
        model.addAttribute("selectedUsername",    null);
        model.addAttribute("firstUsername",       null);
        model.addAttribute("messages",            Collections.emptyList());
        model.addAttribute("otherUser",           null);

        return "message";
    }


    /**
     * 2) Show 1:1 chat thread
     *    URL: GET /messages/{username}
     */
    @GetMapping("/{username}")
    public String showChat(@PathVariable("username") String username,
                           Model model,
                           Principal principal) {
        // who I am?
        String me = principal.getName();
        userService.getByUsernameOrEmail(me);

        // who I'm chatting with
        User other = userService.getByUsernameOrEmail(username);
        
        if (other == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // clear their messages → me as “read”
        messageService.markConversationAsRead(me, username);

        // build the sidebar + thread + dropdown + notification count
        List<ConversationDto> conversations     = messageService.listConversations(me);
        List<Message>         messages          = messageService.getThread(me, username);
        List<User>            allUsers          = userService.findAllExcept(me);
        long                  unreadNotifications = notificationService.unreadCount(me);

        model.addAttribute("conversations",       conversations);
        model.addAttribute("allUsers",            allUsers);
        model.addAttribute("selectedUsername",    username);
        model.addAttribute("firstUsername",       conversations.isEmpty() ? null : conversations.get(0).getUsername());
        model.addAttribute("messages",            messages);
        model.addAttribute("otherUser",           other);
        model.addAttribute("unreadNotifications", unreadNotifications);

        return "message";
    }


    /**
     * 3) Send a new message
     *    URL: POST /messages/send
     */
    @PostMapping("/send")
    public String send(@RequestParam("recipient") String recipientUsername,
                       @RequestParam("content")   String content,
                       Principal principal) {
        User sender    = userService.getByUsernameOrEmail(principal.getName());
        User recipient = userService.getByUsernameOrEmail(recipientUsername);

        messageService.send(sender, recipient, content);

        // create a notification linking back to this chat
        String chatUrl = "/messages/" +
                URLEncoder.encode(sender.getUsername(), StandardCharsets.UTF_8);
        notificationService.createNotification(
                recipient,
                chatUrl,
                "New message from " + sender.getFullName()
        );

        return "redirect:/messages/" +
                URLEncoder.encode(recipientUsername, StandardCharsets.UTF_8);
    }

    /**
     * 4) Mark a message as read
     *    URL: POST /messages/{id}/read
     */
    @PostMapping("/{id}/read")
    public String markRead(@PathVariable("id") Long messageId,
                           @RequestParam("user") String otherUsername) {
        messageService.markRead(messageId);
        return "redirect:/messages/" + otherUsername;
    }


    /**
     * 5) Check for new messages (for polling/long polling)
     *    URL: GET /messages/{username}/updates
     */
    @GetMapping("/{username}/updates")
    @ResponseBody
    public List<Message> getUpdates(@PathVariable("username") String otherUsername,
                                    @RequestParam("lastId") Long lastMessageId,
                                    Principal principal) {
        return messageService.getNewMessages(
                principal.getName(),
                otherUsername,
                lastMessageId
        );
    }

    /**
     * 6) Mark messages as delivered (not just read)
     *    URL: POST /messages/{id}/delivered
     */
    @PostMapping("/{id}/delivered")
    public ResponseEntity<Void> markDelivered(@PathVariable("id") Long messageId) {
        messageService.markDelivered(messageId);
        return ResponseEntity.ok().build();
    }

    /**
     * 7) Delete a message
     *    URL: DELETE /messages/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("id") Long messageId,
                                              Principal principal) {
        messageService.deleteMessage(messageId, principal.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * 8) React to a message
     *    URL: POST /messages/{id}/react
     */
    @PostMapping("/{id}/react")
    public ResponseEntity<Void> reactToMessage(@PathVariable("id") Long messageId,
                                               @RequestParam("emoji") String emoji,
                                               Principal principal) {
        messageService.addReaction(messageId, principal.getName(), emoji);
        return ResponseEntity.ok().build();
    }
}
