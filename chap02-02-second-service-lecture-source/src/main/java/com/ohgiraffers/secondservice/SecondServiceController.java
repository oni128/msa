package com.ohgiraffers.secondservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SecondServiceController {

    @GetMapping("/health")
    public String healthCheck() {
        return "I'm OK2";
    }
}