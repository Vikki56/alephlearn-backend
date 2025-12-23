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
        registry.addEndpoint("/ws/notify")
                .setAllowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "null",
    
                        // PROD domains
                        "https://alephlearn.com",
                        "https://www.alephlearn.com",
                        "https://app.alephlearn.com",
    
                        // Cloudflare Pages default
                        "https://*.pages.dev"
                );
    
        registry.addEndpoint("/ws/notify")
                .setAllowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "null",
    
                        "https://alephlearn.com",
                        "https://www.alephlearn.com",
                        "https://app.alephlearn.com",
                        "https://*.pages.dev"
                )
                .withSockJS();
    }
}