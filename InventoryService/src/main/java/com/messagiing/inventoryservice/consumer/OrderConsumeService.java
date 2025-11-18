package com.messagiing.inventoryservice.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.messagiing.inventoryservice.dto.OrderCreateDto;
import com.messagiing.inventoryservice.producer.BillingEventProducer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.rabbitmq.client.Channel;


import java.nio.charset.StandardCharsets;

@Service
public class OrderConsumeService {


    public static final String QUEUE_NAME = "order.created.inventory.queue";

    private final BillingEventProducer billingEventProducer;
    private final ObjectMapper objectMapper;


    public OrderConsumeService(BillingEventProducer billingEventProducer, ObjectMapper objectMapper) {
        this.billingEventProducer = billingEventProducer;
        this.objectMapper = objectMapper;
    }


    @RabbitListener(queues = QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")
    public void receive(Message message, Channel channel) throws Exception {

        //Deserialize body
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        OrderCreateDto order = objectMapper.readValue(body, OrderCreateDto.class);

        // Read metadata
        String messageId = message.getMessageProperties().getMessageId();
        String correlationId = message.getMessageProperties().getCorrelationId();
        String orderId = (String) message.getMessageProperties().getHeaders().get("orderId");

        System.out.println("Inventory Received OrderId=" + orderId +
                " messageId=" + messageId +
                " correlationId=" + correlationId);

        // Forward to Billing (WITH HEADERS)
        billingEventProducer.sendOrderForBillingEvent(order, messageId, correlationId, orderId);

        // Manual ACK
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
