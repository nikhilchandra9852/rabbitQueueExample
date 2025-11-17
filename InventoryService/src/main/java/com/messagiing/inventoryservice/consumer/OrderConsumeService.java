package com.messagiing.inventoryservice.consumer;


import com.messagiing.inventoryservice.dto.OrderCreateDto;
import com.messagiing.inventoryservice.producer.BillingEventProducer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumeService {


    public static final String QUEUE_NAME = "order.created.inventory.queue";

    private final BillingEventProducer billingEventProducer;


    public OrderConsumeService(BillingEventProducer billingEventProducer) {
        this.billingEventProducer = billingEventProducer;
    }


    @RabbitListener(queues = QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")

    public void receive(OrderCreateDto order) {
        System.out.println("Order Came to Inventory " + order.getOrderId());
        // send the order to the billing
        billingEventProducer.sendOrderForBillingEvent(order);

    }


}
