package com.example.esDemo.user;

import com.example.esDemo.es.HelloService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Component
public class ConsumerManager {

    @DubboReference
    private HelloService helloService;

    public String hello(String name) {
        return helloService.sayHello(name);
    }
}
