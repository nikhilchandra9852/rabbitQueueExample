package com.messagiing.producerservice.controller;


import com.messagiing.producerservice.producer.MessageProducer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v2")
public class MessageController {


    private final MessageProducer producer;

    public MessageController(MessageProducer producer) {
        this.producer = producer;
    }

    @GetMapping("/produces")
    public ResponseEntity<String> sendMessage(@RequestParam String message) {
        producer.sendMessage(message);
        return new ResponseEntity<>("Message Sent",HttpStatusCode.valueOf(200));

    }


}
