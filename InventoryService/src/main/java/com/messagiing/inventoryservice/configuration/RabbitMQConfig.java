package com.messagiing.inventoryservice.configuration;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    // queue names ,Exchange,routing key

    public static final String EXCHANGE_NAME = "order.billing.exchange";
    public static final String QUEUE_NAME = "order.created.billing.queue";
    public static final String ROUTING_KEY = "order.created.billing.routingkey";


    // dlq and retry logic
    public static final String INVENTORY_DLQ = "order.created.inventory.dlq";

    // retry logic
    public static final String INVENTORY_RETRY_1 = "order.created.inventory.retry1";
    public static final String INVENTORY_RETRY_2 = "order.created.inventory.retry2";
    public static final String INVENTORY_RETRY_3 = "order.created.inventory.retry3";


    public static final String EXCHANGE_NAME_INVENTORY = "order.exchange";
//    public static final String QUEUE_NAME = "order.created.inventory.queue";
    public static final String ROUTING_KEY_INVENTORY = "order.created.inventory.routingkey";


    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME)
//                .withArgument("x-dead-letter-exchange",EXCHANGE_NAME)
                .build();
    }

    // create bean fore topic
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // bind the topic to the queue
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setClassMapper(new DefaultClassMapper());
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        // enable publisher confirms

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if(!ack){
                System.out.println("Message NOT delivered to exchange! Cause: " + cause);
            }
        });

        // enable return callback- if fails

        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            System.out.println("Message delivered to exchange! ReturnedMessage: " + returnedMessage);
        });
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setMaxConcurrentConsumers(20); // for scaling
        factory.setPrefetchCount(50); // batch size
        factory.setConcurrentConsumers(5); // max concurrent users
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    // retry logics for inventory queue

    @Bean
    public Queue inventoryRetry1(){
        return QueueBuilder.durable(INVENTORY_RETRY_1)
                .withArgument("x-message-ttl", 10000)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME_INVENTORY)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_INVENTORY)
                .build();
    }

    @Bean
    public Queue inventoryRetry2(){
        return QueueBuilder.durable(INVENTORY_RETRY_2)
                .withArgument("x-message-ttl", 10000)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME_INVENTORY)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_INVENTORY)
                .build();
    }

    // creating DLQ
    @Bean
    public Queue inventoryDLQ(){
        return QueueBuilder.durable(INVENTORY_DLQ).build();
    }

    // create bean fore topic
    @Bean
    public TopicExchange exchange1() {
        return new TopicExchange(EXCHANGE_NAME_INVENTORY);
    }

    @Bean
    public Queue inventoryRetry3(){
        return QueueBuilder.durable(INVENTORY_RETRY_3)
                .withArgument("x-message-ttl", 10000)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME_INVENTORY)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY_INVENTORY)
                .build();
    }

    // inventory DLQ binding
    @Bean
    public Binding inventoryDlqBinding() {
        return BindingBuilder.bind(inventoryDLQ())
                .to(exchange1())
                .with(INVENTORY_DLQ);
    }




}
