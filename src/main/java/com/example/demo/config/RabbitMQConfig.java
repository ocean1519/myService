package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private Integer port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.virtual-host}")
    private String vHost;
    @Bean
    Queue queue() {
        return new Queue("myQueueA", true);
    }
 
    @Bean
    DirectExchange exchange() {
        return new DirectExchange("myExchangeA");
    }
 
    @Bean
    Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("routingKeyA");
    }



    // 定义交换机名称
    public static final String TOPIC_EXCHANGE_NAME = "topic.exchange";

    // 创建 Topic 交换机
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME, true, false);
    }

    // 创建队列 topic.queue
    @Bean
    public Queue queueTopic() {
        return new Queue("topic.queue", true);
    }

    // 绑定队列 topic.queue 到 Topic 交换机
    @Bean
    public Binding bindingQueueA() {
        return BindingBuilder.bind(queueTopic()).to(topicExchange()).with("send.message");
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vHost);
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        return template;
    }
}