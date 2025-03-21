package com.ohgiraffers.userservice.security;

import com.ohgiraffers.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/* 설명. Jwt 토큰 방식의 로그인을 구경할 때 UsernamePasswordAuthenticationToken을 처리할 프로바이더 */
/* 설명. Service 계층을 UserDetailsService 타입으로 바꾸고 옴 */
@Slf4j
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public JwtAuthenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        /* memo:필터에서 넘어온 토큰이 매개변수로 들어옴*/
        /* 설명. 넘어온 토큰에서 사용자가 로그인 시 입력한 id(email)와 pwd(pwd) 추출 */
        String email = authentication.getName();
//        String pwd = authentication.getCredentials().toString();
        String pwd = (String)authentication.getCredentials();

        /* 설명. 해당 email과 일치하는 DB에서 조회 된 회원 */
        /* memo: 조회가 되면 반환 */
        UserDetails userDetails = userService.loadUserByUsername(email);

        if(!passwordEncoder.matches(pwd, userDetails.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }/* memo: 여기에 해당되지 않으면 인증된 사람*/


        /* 설명. 예외가 발생하지 않고 여기 코드가 실행되면 인증된 회원정보 */

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}