package com.ohgiraffers.userservice.vo;

import lombok.Data;

@Data
public class ResponseFindUserVO {
    private String email;
    private String name;
    private String userId;
}
