package com.example.demo.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.config.RedisConfig;
import com.example.demo.entity.TestUser;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.SendReceiveMessage;
import com.example.demo.utils.SafeThreadPoolExample;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@RestController
public class Test {

    private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);

    public static Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    @Autowired
    private SendReceiveMessage receiveMessage;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping(value = "/redis", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String redisTopic(@RequestBody JSONObject jsonObject) {
        String requestBody = jsonObject.getString("msg");
        redisTemplate.convertAndSend(RedisConfig.CHANNEL_GLOBAL_NAME, requestBody);
        return "OK";
    }
    @GetMapping(value = "/connect/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter test(@PathVariable String id) throws IOException {

        LOGGER.info("新用户连接：{}", id);

        SseEmitter sseEmitter = new SseEmitter(0L);

        // 连接断开
        sseEmitter.onCompletion(() -> {
            sseEmitterMap.remove(id);
            redisTemplate.opsForHash().delete("allUser", id);
            LOGGER.info("断开连接:{}", id);
        });

        // 连接超时
        sseEmitter.onTimeout(() -> {
            sseEmitterMap.remove(id);
            redisTemplate.opsForHash().delete("allUser", id);
            LOGGER.info("连接超时:{}", id);
        });

        // 连接报错
        sseEmitter.onError((throwable) -> {
            sseEmitterMap.remove(id);
            redisTemplate.opsForHash().delete("allUser", id);
            LOGGER.info("连接报错:{}", id);
        });

        sseEmitterMap.put(id, sseEmitter);
        redisTemplate.opsForHash().put("allUser", id, "OK");
        LOGGER.info("users:{}", JSON.toJSONString(sseEmitterMap.keySet()));

        // 连接成功需要返回数据，否则会出现待处理状态
        try {
            SendReceiveMessage.MessageData message = new SendReceiveMessage.MessageData();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Map<String, String> allUserMap = redisTemplate.opsForHash().entries("allUser");
            List<User> users = new ArrayList<>();
            if (!CollectionUtils.isEmpty(allUserMap)) {
                allUserMap.forEach((k, v) -> {
                    users.add(User.builder().id(k).status(v).build());
                });
            }
            message.setData(format.format(LocalDateTime.now()));
            message.setUsers(users);
            sseEmitter.send(message, MediaType.APPLICATION_JSON);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sseEmitter;
    }

    @GetMapping(value = "/send/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String sendMessage(@PathVariable String id) throws IOException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("message", "hello " + id);
        receiveMessage.sendOneMessage(json.toJSONString());
        return "OK";
    }

    @PostMapping(value = "/send", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String sendMsg(@RequestBody JSONObject param) throws IOException {
        JSONObject json = new JSONObject();
        json.put("id", param.get("id"));
        json.put("message", param.getString("message"));
        receiveMessage.sendOneMessage(json.toJSONString());
        return "OK";
    }

    @Autowired
    private UserMapper userMapper;

    @PostMapping(value = "/send/test/mysql", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String sendTestMysql() {
        userMapper.insert(TestUser.builder().username("test" + System.currentTimeMillis()).password(String.valueOf(System.currentTimeMillis())).build());
        Map<String, Object> map = new HashMap<>();
        map.put("id", 2);
        List<TestUser> resultList = userMapper.selectByMap(map);
        TestUser testUser = userMapper.selectByUsernameAndPassword("test1", "password1");

        Page pageResult = userMapper.selectPage(new Page(1, 10), null);
        Page pageResult1 = userMapper.selectAll(new Page(1, 10), TestUser.builder().id(1).build());
        return "OK";
    }

    @GetMapping(value = "/send/all/{message}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String sendAllMessage(@PathVariable String message) {

        SendReceiveMessage.MessageData data = new SendReceiveMessage.MessageData();
        data.setData(message);
        receiveMessage.sendMessage(data);
        return "OK";
    }

    @GetMapping(value = "/test/thread", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String testThread() {
        ExecutorService executor = SafeThreadPoolExample.ExecutorInstance.INSTANCE.getExecutor();
        List<Integer> userIds = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            userIds.add(i);
        }

        List<Supplier<String>> tasks = new ArrayList<>();
        for (Integer userId : userIds) {
            Supplier supplier = () -> sendRequest(userId);
            tasks.add(supplier);
        }
        JSON.toJSONString(SafeThreadPoolExample.doTask(tasks, new SafeThreadPoolExample.Callback<String>() {
            @Override
            public void onSuccess(String result) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(result + " +++++++++++");
                    }
                });
            }
        }));

        List<Supplier<String>> tasks1 = new ArrayList<>();
        for (Integer userId : userIds) {
            Supplier supplier = () -> sendRequest(userId);
            tasks1.add(supplier);
        }

        JSON.toJSONString(SafeThreadPoolExample.doTask(tasks1, new SafeThreadPoolExample.Callback<String>() {
            @Override
            public void onSuccess(String result) {
                SafeThreadPoolExample.ExecutorInstance.INSTANCE.getExecutor().submit(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(result + System.currentTimeMillis() + " +++++++++++");
                    }
                });
            }
        }));

        //未添加shutdown
        return "OK";
    }

    private static String sendRequest(int index) {
        System.out.println("Sending request: " + index);
        try {
//            if (index == 500) {
//                Thread.sleep(10000);
//            }
            Thread.sleep(50);
        } catch (Exception e) {
            System.out.println("请求异常" + e);
            throw new RuntimeException("异常", e);
        }
        return "返回结果: " + index;
    }
}

