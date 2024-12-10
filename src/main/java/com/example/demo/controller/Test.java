package com.example.demo.controller;

import com.alibaba.fastjson2.JSON;
import com.example.demo.config.RedisConfig;
import com.example.demo.service.SendReceiveMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class Test {

    private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

    public static Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    @Autowired
    private SendReceiveMessage receiveMessage;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping(value = "/redis", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String redisTopic() throws IOException {
        redisTemplate.convertAndSend(RedisConfig.CHANNEL_GLOBAL_NAME, "test" + System.currentTimeMillis());
        return "OK";
    }
    @GetMapping(value = "/connect/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter test(@PathVariable String id) throws IOException {

        LOGGER.info("新用户连接：{}", id);

        SseEmitter sseEmitter = new SseEmitter(0L);

        // 连接成功需要返回数据，否则会出现待处理状态
        try {
            sseEmitter.send(SseEmitter.event().comment("welcome"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 连接断开
        sseEmitter.onCompletion(() -> {
            sseEmitterMap.remove(id);
            LOGGER.info("断开连接:{}", id);
        });
        System.out.println("timeout:" + sseEmitter.getTimeout());
        // 连接超时
        sseEmitter.onTimeout(() -> {
            sseEmitterMap.remove(id);
            LOGGER.info("连接超时:{}", id);
        });

        // 连接报错
        sseEmitter.onError((throwable) -> {
            sseEmitterMap.remove(id);
            LOGGER.info("连接报错:{}", id);
        });

        sseEmitterMap.put(id, sseEmitter);
        LOGGER.info("users:{}", JSON.toJSONString(sseEmitterMap.keySet()));

        SendReceiveMessage.Message message = new SendReceiveMessage.Message();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        message.setData(LocalDateTime.now().format(format));
        receiveMessage.sendMessage(message);
        return sseEmitter;
    }

    @GetMapping(value = "/send/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String sendMessage(@PathVariable String id) throws IOException {

        receiveMessage.sendOneMessage(id, "hello " + id);
        return "OK";
    }

    @GetMapping(value = "/send/all/{message}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String sendAllMessage(@PathVariable String message) {

        SendReceiveMessage.Message data = new SendReceiveMessage.Message();
        data.setData(message);
        receiveMessage.sendMessage(data);
        return "OK";
    }

}
