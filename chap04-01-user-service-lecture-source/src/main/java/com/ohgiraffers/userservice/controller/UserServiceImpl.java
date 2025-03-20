package com.ohgiraffers.userservice.controller;

import com.ohgiraffers.userservice.aggregate.UserEntity;
import com.ohgiraffers.userservice.dto.UserDTO;
import com.ohgiraffers.userservice.repository.UserRepository;
import com.ohgiraffers.userservice.service.UserService;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    ModelMapper modelMapper;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public void registUser(UserDTO userDTO) {

        /* 설명. 회원 가입 할 때 고유 번호 할당 */
        userDTO.setUserId(UUID.randomUUID().toString());

        UserEntity registUser = modelMapper.map(userDTO, UserEntity.class);
        userRepository.save(registUser); /* entityManager에게 날라감 -> Manager자신의 영속성 컨텍스트로 들어감*/
    }
}
