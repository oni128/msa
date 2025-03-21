package com.ohgiraffers.userservice.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String email;
    private String name;
    private String pwd;

    /* 설명. 회원가입 진행하며 추가됨 */
    private String userId;
}