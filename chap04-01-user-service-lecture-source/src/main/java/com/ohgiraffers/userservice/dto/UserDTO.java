package com.ohgiraffers.userservice.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String email;
    private String name;
    private String pwd;

    /* 설명. 회원 가입 할 때 고유 번호 할당 */
    private String userId;
}
