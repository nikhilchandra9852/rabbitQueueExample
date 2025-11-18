package com.messagiing.billingservice.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.messagiing.billingservice.dto.OrderCreateDto;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class OrderConsumeService {


    public static final String QUEUE_NAME = "order.created.billing.queue";
    private final ObjectMapper mapper;

    public OrderConsumeService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @RabbitListener(queues = QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")
    public void receive(Message message, Channel channel) throws Exception {

        // Deserialize body
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        OrderCreateDto orderCreateDto = mapper.readValue(messageBody, OrderCreateDto.class);

        // reading meta data
        String orderId = message.getMessageProperties().getHeader("orderId");
        String correlationId = message.getMessageProperties().getCorrelationId();
        String messageId = message.getMessageProperties().getMessageId();



        System.out.println("Order Came to Billing and Billing happens " + orderId +
                " messageId=" + messageId +
                " correlationId=" + correlationId);

        // manual acknowledgement

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }


}
