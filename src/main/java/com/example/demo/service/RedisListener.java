package com.example.demo.service;

import com.example.demo.config.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisListener implements MessageListener {


    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Autowired
    public void setRedisMessageListenerContainer(RedisMessageListenerContainer redisMessageListenerContainer) {
        this.redisMessageListenerContainer = redisMessageListenerContainer;
        this.redisMessageListenerContainer.addMessageListener(this, ChannelTopic.of(RedisConfig.CHANNEL_GLOBAL_NAME));
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 解析和处理消息
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        System.out.println("Received message from channel " + channel + ": " + body);
    }
}