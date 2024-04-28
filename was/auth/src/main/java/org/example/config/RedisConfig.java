package org.example.config;

import org.example.application.MemberEventHandler;
import org.example.domain.event.AddIngredientEvent;
import org.example.domain.event.RemoveIngredientEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    @Qualifier("addIngredientTemplate")
    RedisTemplate<String, Object> addIngredientTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(AddIngredientEvent.class)); // 첫 번째 토픽에 대한 ValueSerializer
        return redisTemplate;
    }

    @Bean
    @Qualifier("removeIngredientTemplate")
    RedisTemplate<String, Object> removeIngredientTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(RemoveIngredientEvent.class)); // 두 번째 토픽에 대한 다른 ValueSerializer
        return redisTemplate;
    }

    //리스너어댑터 설정
    @Bean
    MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(new MemberEventHandler());
    }

    //컨테이너 설정
    @Bean
    RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        container.addMessageListener(messageListenerAdapter(), removeIngredientTopic());
        container.addMessageListener(messageListenerAdapter(), addIngredientTopic());
        return container;
    }

    @Bean
    ChannelTopic addIngredientTopic() {
        return new ChannelTopic("AddIngredient");
    }

    @Bean
    ChannelTopic removeIngredientTopic() {
        return new ChannelTopic("RemoveIngredient");
    }
}
