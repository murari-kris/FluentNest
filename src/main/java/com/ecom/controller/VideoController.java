package com.ecom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;

import java.util.UUID;

@Controller
public class VideoController {

    /**
     * @param roomId - Optional: if not provided, we generate a random one for a new session.
     */
    @GetMapping("/video")
    public String videoPage(@RequestParam(value = "roomId", required = false) String roomId, 
                            Model model, 
                            Authentication authentication) {
        
        // 1. Get the username from Spring Security (e.g., learner123)
        String userId = (authentication != null) ? authentication.getName() : "Guest_" + UUID.randomUUID().toString().substring(0, 5);

        // 2. Fallback for Room ID: If no room is specified, create a new session ID
        if (roomId == null || roomId.isEmpty()) {
            roomId = "english-session-" + UUID.randomUUID().toString().substring(0, 8);
        }

        model.addAttribute("roomId", roomId);
        model.addAttribute("userId", userId);

        return "video"; 
    }
}