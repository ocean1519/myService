package com.example.demo.timer;


import com.example.demo.entity.User;
import com.example.demo.service.SendReceiveMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class SendMessageTask {

    @Autowired
    private SendReceiveMessage receiveMessage;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 定时执行 秒 分 时 日 月 周
     */
    @Scheduled(cron = "*/10 * * * * *")  // 间隔60秒
    public void sendMessageTask() {
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
        receiveMessage.sendMessage(message);
    }
}


