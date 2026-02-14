package com.ecom.controller;

import com.ecom.repository.MessageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class ChatPageController {

    private final MessageRepository messageRepository;

    public ChatPageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping("/chat/{username}")
    public String chatPage(@PathVariable String username,
                           Principal principal,
                           Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        String me = principal.getName();

        if (me.equals(username)) {
            return "redirect:/home";
        }

        model.addAttribute("chatWith", username);
        model.addAttribute(
                "messages",
                messageRepository.getConversation(me, username)
        );

        return "chat";
    }
}
