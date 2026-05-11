package com.running.runapp.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Flutter가 최초로 웹소켓 연결을 맺는 엔드포인트
        registry.addEndpoint("/ws/run")
                .setAllowedOriginPatterns("*"); // 로컬 테스트이니 일단 모두 허용
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 서버 -> 클라이언트로 메시지를 보낼 때 사용할 prefix (구독)
        registry.enableSimpleBroker("/topic");

        // 클라이언트 -> 서버로 메시지를 보낼 때 사용할 prefix (발행)
        registry.setApplicationDestinationPrefixes("/app");
    }

}
