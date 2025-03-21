package com.ohgiraffers.userservice.vo;

import lombok.Data;

/* 설명. 회원가입 요청 때와 달리 응답 시에는 비밀번호 대신 서버가 생성한 고유 회원 번호(userId)를 돌려준다. */
@Data
public class ResponseRegistUserVO {
    private String email;
    private String name;
    private String userId;
}