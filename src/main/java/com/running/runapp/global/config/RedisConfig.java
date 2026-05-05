package com.running.runapp.global.config;

import com.running.runapp.domain.running.service.RedisSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // Key는 일반 문자열로 저장
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // Value(LocationMessage 객체)는 JSON 직렬화 도구를 사용하여 저장 ✅ 핵심!
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    // 1. Redis에서 메시지가 오면 Subscriber의 onMessage 메서드를 실행하라고 연결
    @Bean
    public MessageListener listenerAdapter(RedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    // 2. 어떤 채널을 구독할지 설정 (컨테이너)
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisSubscriber subscriber
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);



        // 핵심: "group:1004", "group:999" 등 'group:'으로 시작하는 모든 방의 채널을 구독!
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(subscriber, "onMessage");
        container.addMessageListener(listenerAdapter, new PatternTopic("group:*"));

        return container;
    }
}
