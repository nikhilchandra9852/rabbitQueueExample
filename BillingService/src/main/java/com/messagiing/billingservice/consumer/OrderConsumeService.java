package com.messagiing.billingservice.consumer;


import com.messagiing.billingservice.dto.OrderCreateDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumeService {


    public static final String QUEUE_NAME = "order.created.billing.queue";


    @RabbitListener(queues = QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")

    public void receive(OrderCreateDto order) {
        System.out.println("Order Came to Billing and Billing happens " + order.getOrderId());

    }


}
