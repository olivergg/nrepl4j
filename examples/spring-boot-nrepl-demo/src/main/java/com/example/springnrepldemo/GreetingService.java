package com.example.springnrepldemo;

import org.springframework.stereotype.Component;

@Component
public class GreetingService {

    private final String prefix = "Hello, ";

    public String greet(String name) {
        return prefix + name + "!";
    }
}
