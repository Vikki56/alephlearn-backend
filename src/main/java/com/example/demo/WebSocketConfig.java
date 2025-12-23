package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler chatHandler;

    public WebSocketConfig(ChatHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatHandler, "/ws/chat")
                // Allow any localhost/127.0.0.1 port in dev
                .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*");
                // no credentials on WS, patterns are fine
    }
}
