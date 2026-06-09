package com.running.runapp.domain.running.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.running.runapp.domain.running.dto.LocationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            //Redis에서 온 JSON 데이터를 객체로 변환
            String publishMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());
            LocationMessage locationMessage = objectMapper.readValue(publishMessage, LocationMessage.class);

            log.debug("Redis message broadcast: groupId={}, memberId={}", locationMessage.getGroupId(), locationMessage.getMemberId());

            // 해당 방(/topic/group/{groupId}을 구독 중인 웹소켓 클라이언트들에게 쏴줌
            String destination = "/topic/group/" + locationMessage.getGroupId().toString();
            messagingTemplate.convertAndSend(destination, locationMessage);

        } catch (Exception e) {
            log.error("Redis Subscribe Error", e);
        }
    }

}
