package com.ecom.webrtc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SignalingHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper = new ObjectMapper();
    
    // Queue for people waiting to practice
    private final CopyOnWriteArrayList<WebSocketSession> waitingUsers = new CopyOnWriteArrayList<>();
    
    // Map to link paired sessions
    private final Map<String, WebSocketSession> pairs = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> data = mapper.readValue(message.getPayload(), Map.class);
        String type = (String) data.get("type");

        // 1. Logic for matching two random users
        if ("find_partner".equals(type)) {
            if (!waitingUsers.isEmpty() && !waitingUsers.contains(session)) {
                WebSocketSession partner = waitingUsers.remove(0);
                
                // Link them
                pairs.put(session.getId(), partner);
                pairs.put(partner.getId(), session);

                // Notify both that they can start WebRTC
                session.sendMessage(new TextMessage("{\"type\": \"match_found\"}"));
                partner.sendMessage(new TextMessage("{\"type\": \"match_found\"}"));
                System.out.println("Match Created: " + session.getId() + " + " + partner.getId());
            } else if (!waitingUsers.contains(session)) {
                waitingUsers.add(session);
                System.out.println("User waiting in lobby: " + session.getId());
            }
            return;
        }

        // 2. Relay logic (Offer, Answer, ICE, Chat)
        WebSocketSession partner = pairs.get(session.getId());
        if (partner != null && partner.isOpen()) {
            partner.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        waitingUsers.remove(session);
        WebSocketSession partner = pairs.remove(session.getId());
        if (partner != null) {
            pairs.remove(partner.getId());
            if (partner.isOpen()) {
                partner.sendMessage(new TextMessage("{\"type\": \"partner_left\"}"));
            }
        }
        System.out.println("User left: " + session.getId());
    }
}