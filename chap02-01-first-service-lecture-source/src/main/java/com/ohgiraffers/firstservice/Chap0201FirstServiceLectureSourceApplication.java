package com.ohgiraffers.firstservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient  // 추가 안해도 eureka client는 동작하지만 어노테이션 추가하자.
public class Chap0201FirstServiceLectureSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(Chap0201FirstServiceLectureSourceApplication.class, args);
    }

}