package com.ecom.websocket;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class VideoSignalHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> waitingRoom = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> activeMatches = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JSONObject json = new JSONObject(message.getPayload());
        String type = json.getString("type");

        if ("find_partner".equals(type)) {
            handleMatching(session);
        } else {
            // Relay everything else (Chat, Offer, Answer, ICE) to the partner
            WebSocketSession partner = activeMatches.get(session.getId());
            if (partner != null && partner.isOpen()) {
                partner.sendMessage(new TextMessage(json.toString()));
            }
        }
    }

    private void handleMatching(WebSocketSession session) throws Exception {
        var partnerEntry = waitingRoom.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(session.getId()))
                .findFirst();

        if (partnerEntry.isPresent()) {
            WebSocketSession partner = partnerEntry.get().getValue();
            waitingRoom.remove(partner.getId());
            activeMatches.put(session.getId(), partner);
            activeMatches.put(partner.getId(), session);

            JSONObject msg = new JSONObject().put("type", "match_found");
            session.sendMessage(new TextMessage(msg.toString()));
            partner.sendMessage(new TextMessage(msg.toString()));
        } else {
            waitingRoom.put(session.getId(), session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        waitingRoom.remove(session.getId());
        WebSocketSession partner = activeMatches.remove(session.getId());
        if (partner != null && partner.isOpen()) {
            partner.sendMessage(new TextMessage(new JSONObject().put("type", "partner_left").toString()));
        }
    }
}