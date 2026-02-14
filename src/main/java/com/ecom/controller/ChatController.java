package com.ecom.controller;

import com.ecom.model.ChatMessage;
import com.ecom.repository.MessageRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;

    public ChatController(SimpMessagingTemplate messagingTemplate,
                          MessageRepository messageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
    }

    @MessageMapping("/chat.private")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage,
                                   Principal principal) {

        try {
            // 🔐 Secure sender
            chatMessage.setSender(principal.getName());

            // 💾 Save message
            ChatMessage saved = messageRepository.save(chatMessage);

            // 📤 Send to receiver
            messagingTemplate.convertAndSendToUser(
                    saved.getReceiver(),
                    "/queue/messages",
                    saved
            );

            // 📤 Send to sender (instant UI)
            messagingTemplate.convertAndSendToUser(
                    saved.getSender(),
                    "/queue/messages",
                    saved
            );

        } catch (Exception e) {
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/errors",
                    "❌ Message failed to send"
            );
        }
    }
}
