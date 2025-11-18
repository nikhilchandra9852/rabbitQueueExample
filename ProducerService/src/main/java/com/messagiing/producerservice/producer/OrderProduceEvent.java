package com.messagiing.producerservice.producer;


import com.messagiing.producerservice.configuration.RabbitMQConfig;
import com.messagiing.producerservice.dto.OrderCreateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderProduceEvent {

    private static final Logger log = LoggerFactory.getLogger(OrderProduceEvent.class);

    private final RabbitTemplate rabbitTemplate;

    public OrderProduceEvent(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void produceOrder(OrderCreateDto orderId) {
        // handle correlationId and messageId

        String correlationId = orderId.getOrderId();
        String messageId = UUID.randomUUID().toString();
        // convet

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                orderId,
                message -> {
                    message.getMessageProperties().setCorrelationId(correlationId);
                    message.getMessageProperties().setMessageId(messageId);
                    message.getMessageProperties().setHeader("orderId", orderId.getOrderId());
                    return message;
                }
        );
        log.info("Published order event: orderId={}, messageId={}, correlationId={}",
                orderId.getOrderId(), messageId, correlationId);
    }
}
