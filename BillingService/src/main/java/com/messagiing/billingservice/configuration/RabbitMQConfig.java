package com.messagiing.billingservice.configuration;


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

    public static final String EXCHANGE_NAME = "order.billing.exchange";
    public static final String QUEUE_NAME = "order.created.billing.queue";
    public static final String ROUTING_KEY = "order.created.billing.routingkey";



    // dlq and retry logic
    public static final String BILLING_DLQ = "order.created.billing.dlq";

    // retry logic
    public static final String BILLING_RETRY_1 = "order.created.billing.retry1";
    public static final String BILLING_RETRY_2 = "order.created.billing.retry2";
    public static final String BILLING_RETRY_3 = "order.created.billing.retry3";



    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setClassMapper(new DefaultClassMapper());
        return converter;
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

    // creating DLQ
    @Bean
    public Queue billingDLQ(){
        return QueueBuilder.durable(BILLING_DLQ).build();
    }



    // retry logics for inventory queue

    @Bean
    public Queue billingRetry1(){
        return QueueBuilder.durable(BILLING_RETRY_1)
                .withArgument("x-message-ttl", 10000)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue billingRetry2(){
        return QueueBuilder.durable(BILLING_RETRY_2)
                .withArgument("x-message-ttl", 10000)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue billingRetry3(){
        return QueueBuilder.durable(BILLING_RETRY_3)
                .withArgument("x-message-ttl", 10000)
                .withArgument("x-dead-letter-exchange", EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", ROUTING_KEY)
                .build();
    }

    // create bean fore topic
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }


    // inventory DLQ binding
    @Bean
    public Binding inventoryDlqBinding() {
        return BindingBuilder.bind(billingDLQ())
                .to(exchange())
                .with(BILLING_DLQ);
    }
}
