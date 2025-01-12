package com.example.game_lobby_server.support;

import com.example.game_lobby_server.service.JwtSecretKeyService;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtSecretKeyService jwtSecretKeyService;

    public WebSocketConfig(JwtSecretKeyService jwtSecretKeyService) {
        this.jwtSecretKeyService = jwtSecretKeyService;
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // クライアントへの送り口の設定
        config.enableSimpleBroker("/topic");

        // クライアントからの受け取り口の設定
        config.setApplicationDestinationPrefixes("/lobby");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // クライアントが最初にWebSocketを繋ぐ際の繋ぎ口
        registry.addEndpoint("/lobby-websocket")
                .setAllowedOriginPatterns("http://localhost:3000")
                .withSockJS();
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new JwtChannelInterceptor(jwtSecretKeyService));
    }
}
