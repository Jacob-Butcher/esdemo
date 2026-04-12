package com.example.esDemo.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AiManager {

    @Autowired
    private AiService aiService;

    public String chat(String message){
        return aiService.chat(message);
    }
}
