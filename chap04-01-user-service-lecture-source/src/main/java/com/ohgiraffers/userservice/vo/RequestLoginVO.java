package com.ohgiraffers.userservice.vo;

import lombok.Data;

@Data
public class RequestLoginVO {
    private String email;
    private String pwd;
}