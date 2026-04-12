package com.example.esDemo.controller;

import com.example.esDemo.ai.AiManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiController {
    @Autowired
    private AiManager aiManager;

    @GetMapping("/chat")
    public String chat(String message) {
        return aiManager.chat(message);
    }
}
