package com.messagiing.producerservice.consumer;


import com.messagiing.producerservice.configuration.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    // add the listner method

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        System.out.println("Received Message " + message);
    }

}
