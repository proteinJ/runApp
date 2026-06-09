package com.running.runapp.domain.running.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.running.runapp.domain.running.dto.LocationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LocationController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 클라이언트가 /app/group/{groupId}/location 으로 위치를 보내면
     * Redis "group:{groupId}" 채널에 publish → RedisSubscriber가 수신해
     * /topic/group/{groupId} 구독자 전체에게 브로드캐스트
     */
    @MessageMapping("/group/{groupId}/location")
    public void receiveLocation(
            @DestinationVariable Long groupId,
            LocationMessage message
    ) {
        message.setGroupId(groupId);
        log.info("[WS] receiveLocation groupId={} memberId={}", groupId, message.getMemberId());
        try {
            String json = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend("group:" + groupId, json);
            log.info("[WS] Redis publish → group:{} json={}", groupId, json);
        } catch (JsonProcessingException e) {
            log.error("Location message serialization failed", e);
        }
    }
}
