package com.ecom.config;

import com.ecom.websocket.VideoSignalHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class VideoSocketConfig implements WebSocketConfigurer {

    private final VideoSignalHandler videoSignalHandler;

    // Inject the handler instead of creating it with 'new'
    public VideoSocketConfig(VideoSignalHandler videoSignalHandler) {
        this.videoSignalHandler = videoSignalHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    	// Make sure this matches exactly what is in your JavaScript
    	registry.addHandler(videoSignalHandler, "/ws") // If this says "/video-chat", JS will fail.
    	        .setAllowedOrigins("*");// In production, replace * with your domain
    }
}