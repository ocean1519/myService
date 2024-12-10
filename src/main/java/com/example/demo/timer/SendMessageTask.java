package com.example.demo.timer;


import com.example.demo.service.SendReceiveMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class SendMessageTask {

    @Autowired
    private SendReceiveMessage receiveMessage;

    /**
     * 定时执行 秒 分 时 日 月 周
     */
    @Scheduled(cron = "*/5 * * * * *")  // 间隔60秒
    public void sendMessageTask() {
        SendReceiveMessage.Message message = new SendReceiveMessage.Message();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        message.setData("心跳检测: " + LocalDateTime.now().format(format));
        receiveMessage.sendMessage(message);


    }
}


