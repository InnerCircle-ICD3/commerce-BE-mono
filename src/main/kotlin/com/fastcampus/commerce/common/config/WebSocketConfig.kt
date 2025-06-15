package com.fastcampus.commerce.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {


    override fun configureMessageBroker(registry : MessageBrokerRegistry){
        registry.enableSimpleBroker("/sub")
        registry.setApplicationDestinationPrefixes("/pub")
        registry.setUserDestinationPrefix("/user")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // SockJS 지원 엔드포인트
        registry
            .addEndpoint("/chat")
            .setAllowedOriginPatterns("http://localhost:8080", "http://localhost:3000", "http://localhost:5173")
            .withSockJS()

        // 순수 WebSocket 엔드포인트
        registry
            .addEndpoint("/chat")
            .setAllowedOriginPatterns("http://localhost:8080", "http://localhost:3000", "http://localhost:5173")
    }
}
