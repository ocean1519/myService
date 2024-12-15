package com.example.demo.controller;

import com.alibaba.fastjson2.JSON;
import com.example.demo.config.RedisConfig;
import com.example.demo.service.EsUserService;
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
public class Es {

    private static final Logger LOGGER = LoggerFactory.getLogger(Es.class);

    @Autowired
    private EsUserService esUserService;

    @GetMapping(value = "/es/test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String test() throws IOException {

        esUserService.indexExists();
        return "OK";
    }

}

