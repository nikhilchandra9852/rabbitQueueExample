package com.messagiing.producerservice.producer;


import com.messagiing.producerservice.configuration.RabbitMQConfig;
import com.messagiing.producerservice.dto.OrderCreateDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProduceEvent {

    private final RabbitTemplate rabbitTemplate;

    public OrderProduceEvent(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void produceOrder(OrderCreateDto orderId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                orderId
        );
        System.out.println("Order produced with id " + orderId);
    }
}
