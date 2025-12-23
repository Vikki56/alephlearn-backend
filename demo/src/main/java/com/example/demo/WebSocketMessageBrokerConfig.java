package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is what SockJS + STOMP client should connect to
        registry.addEndpoint("/ws/notify")
                .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*");

        // SockJS fallback endpoint
        registry.addEndpoint("/ws/notify")
                .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Client subscribes here
        registry.enableSimpleBroker("/topic", "/queue");

        // If later you send from client to server using STOMP (optional)
        registry.setApplicationDestinationPrefixes("/app");
    }
}