package com.messagiing.producerservice.controller;


import com.messagiing.producerservice.dto.OrderCreateDto;
import com.messagiing.producerservice.producer.OrderProduceEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class OrderController {


    private final OrderProduceEvent orderProduceEvent;

    public OrderController(OrderProduceEvent orderProduceEvent) {
        this.orderProduceEvent = orderProduceEvent;
    }

    @PostMapping("/order")
    public ResponseEntity<String> getOrder(@RequestBody OrderCreateDto orderCreateDto) {
        orderProduceEvent.produceOrder(orderCreateDto);
        return ResponseEntity.ok(orderCreateDto.toString());
    }


}
