package com.ohgiraffers.userservice.aggregate;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="tbl_member")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;                    // 회원번호

    @Column(name="email", nullable=false, length=50, unique=true)
    private String email;               // 회원의 이메일(ID 개념)

//    @Column(name="pwd", nullable=false)
//    private String pwd;                 // 회원의 비밀번호

    @Column(name="encrypted_pwd", nullable=false)
    private String encryptedPwd;

    @Column(name="name", nullable=false, length=50)
    private String name;                // 회원의 실제 이름

    @Column(name="userId", nullable=false, unique=true)
    private String userId;              // 회원 가입 이후 자동으로 발생 할 고유 번호(일종의 닉네임)

}