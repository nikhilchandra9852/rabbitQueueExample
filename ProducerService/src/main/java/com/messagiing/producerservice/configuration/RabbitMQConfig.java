package com.messagiing.producerservice.configuration;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Configuration
public class RabbitMQConfig {

    // queue names ,Exchange,routing key

    public static final String EXCHANGE_NAME = "order.exchange";
    public static final String QUEUE_NAME = "order.created.inventory.queue";
    public static final String ROUTING_KEY = "order.created.inventory.routingkey";

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME)
//                .withArgument("x-dead-letter-exchange",EXCHANGE_NAME)
//                .withArgument("x-dead-letter-routing-key",INVENTORY_DLQ)
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


}
