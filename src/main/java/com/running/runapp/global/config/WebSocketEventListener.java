package com.running.runapp.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketConnect(SessionConnectedEvent event) {
        String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
        log.info("WebSocket connected: sessionId={}", sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        log.info("WebSocket disconnected: sessionId={}", event.getSessionId());
    }
}
