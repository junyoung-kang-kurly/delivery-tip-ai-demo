package com.example.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class DeliveryTipController {

    @Autowired
    DeliveryTipService deliveryTipService;

    @GetMapping(value = "/tip")
    String stream(@RequestParam String query) {
        return deliveryTipService.query(query);
    }
}
