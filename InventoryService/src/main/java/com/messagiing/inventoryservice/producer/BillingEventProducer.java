package com.messagiing.inventoryservice.producer;

import com.messagiing.inventoryservice.configuration.RabbitMQConfig;
import com.messagiing.inventoryservice.dto.OrderCreateDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class BillingEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public BillingEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrderForBillingEvent(OrderCreateDto order) {
        // checks if particular product Id is present or not
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                order
        );
        System.out.println("Sent Order For Billing Event");
    }
}
