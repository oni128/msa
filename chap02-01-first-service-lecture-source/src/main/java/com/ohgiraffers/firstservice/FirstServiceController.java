package com.ohgiraffers.firstservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
//@RequestMapping("/first-service")
public class FirstServiceController {

    private Environment env;

    @Autowired
    public FirstServiceController(Environment env) {
        this.env = env;
    }

    @GetMapping("/health")
    public String healthCheck() {

        /* 설명. Environment를 활용해서 local.server.port를 가져오면 동적으로 할당된 실제 포트번호 확인 가능 */
        return "I'm OK, 포트는 " + env.getProperty("local.server.port");
    }

    @GetMapping("/messsage")
    public String message(@RequestHeader("first-request") String header) {
        log.info("넘어온 헤더값: {}", header);
        return "First Servicce Message";
    }
}