package com.example.demo.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.demo.controller.Test.sseEmitterMap;

@Component
public class SendReceiveMessage implements RabbitTemplate.ConfirmCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendReceiveMessage.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            System.out.println("回调消息成功消费:" + correlationData.toString());
        } else {
            System.out.println("回调消息消费失败:" + cause);
        }
    }


    public void sendMessage(SendReceiveMessage.MessageData message) {
        rabbitTemplate.setConfirmCallback(this);

        rabbitTemplate.convertAndSend("topic.exchange", "send.message", new Message(JSON.toJSONString(message).getBytes(), new MessageProperties()), new CorrelationData("广播消息" + System.currentTimeMillis()));
    }

    public void sendOneMessage(String message) throws IOException {
        rabbitTemplate.convertAndSend("myExchangeA", "routingKeyA", new Message(JSON.toJSONString(message).getBytes(), new MessageProperties()), new CorrelationData("单个消息=" + System.currentTimeMillis()));
    }

    @RabbitListener(queues = "myQueueA", ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, org.springframework.amqp.core.Message messageObj) throws Exception {
        //消息确认
        // 1.开启手动确认模式spring.rabbitmq.listener.simple.acknowledge-mode=manual或者注解ackMode = "MANUAL"
        // 2.channel.basicAck(messageObj.getMessageProperties().getDeliveryTag(), false); getDeliveryTag=唯一标识;false=不批量确认
        // 3.channel.basicNack(messageObj.getMessageProperties().getDeliveryTag(), false, false); getDeliveryTag=唯一标识;false=不批量确认;false不重回队列
        //广播消息不确认,重启还会收到消息
        channel.basicNack(messageObj.getMessageProperties().getDeliveryTag(), false, false);
        LOGGER.info("收到单个消息：{}", message);

        JSONObject mjo = JSON.parseObject(message);
        String id = mjo.getString("id");
        if (sseEmitterMap.containsKey(id)) {
            SseEmitter sseEmitter = sseEmitterMap.get(id);
            Map<String, String> map = new HashMap<>();
            map.put("data", message);
            LOGGER.info("userId: {}", id);
            sseEmitter.send(map, MediaType.APPLICATION_JSON);
        }
    }

    @RabbitListener(queues = "queue.a", ackMode = "MANUAL")
    public void receiveQueueA(String message, Channel channel,  org.springframework.amqp.core.Message messageObj) throws IOException {
        LOGGER.info("收到广播消息:{}", message);
        //消息确认
        // 1.开启手动确认模式spring.rabbitmq.listener.simple.acknowledge-mode=manual或者注解ackMode = "MANUAL"
        // 2.channel.basicAck(messageObj.getMessageProperties().getDeliveryTag(), false); getDeliveryTag=唯一标识;false=不批量确认
        // 3.channel.basicNack(messageObj.getMessageProperties().getDeliveryTag(), false, false); getDeliveryTag=唯一标识;false=不批量确认;false不重回队列
        //广播消息不确认,重启还会收到消息

        MessageData m = JSON.parseObject(message, MessageData.class);
        m.setTotal(sseEmitterMap.size());

        sseEmitterMap.forEach((id, sseEmitter) -> {
            try {
                LOGGER.info("userId: " + id);
                sseEmitter.send(m, MediaType.APPLICATION_JSON_UTF8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        //int i = 0/0;//如果异常,消息不会重回队列,下次启动还会收到消息
        channel.basicNack(messageObj.getMessageProperties().getDeliveryTag(), false, false);
    }

    public static class MessageData<T> {
        private T data;
        private Integer total;

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }
    }
}
