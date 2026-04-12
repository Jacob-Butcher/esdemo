package com.example.esDemo.controller;

import com.example.esDemo.user.ConsumerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    @Autowired
    private ConsumerManager helloService;

    @GetMapping("/hello")
    public String hello(String name) {
        return helloService.hello(name);
    }
}
