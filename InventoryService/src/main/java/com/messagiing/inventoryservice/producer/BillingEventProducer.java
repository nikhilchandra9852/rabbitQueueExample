package com.messagiing.inventoryservice.producer;

import com.messagiing.inventoryservice.configuration.RabbitMQConfig;
import com.messagiing.inventoryservice.dto.OrderCreateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BillingEventProducer {

    private static final Logger log = LoggerFactory.getLogger(BillingEventProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public BillingEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrderForBillingEvent(OrderCreateDto order, String messageId, String correlationId, String orderId) {
        // checks if particular product Id is present or not
        // convet

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                order,
                message -> {
                    message.getMessageProperties().setCorrelationId(correlationId);
                    message.getMessageProperties().setMessageId(messageId);
                    message.getMessageProperties().setHeader("orderId", order.getOrderId());
                    return message;
                }
        );
        log.info("Published order event: orderId={}, messageId={}, correlationId={}",
                order.getOrderId(), messageId, correlationId);
    }
}
