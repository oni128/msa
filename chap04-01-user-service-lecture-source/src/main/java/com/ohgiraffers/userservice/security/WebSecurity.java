package com.ohgiraffers.userservice.security;

import jakarta.servlet.Filter;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/* 설명. WebSecurityConfigurerAdapter를 상속 받거나 @EnableWebSeciroty를 쓰는 예제는 옛날 방식 -> Bean을 이용하여 등록하자 */
@Configuration
public class WebSecurity {

    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    public WebSecurity(JwtAuthenticationProvider jwtAuthenticationProvider) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new providerManager(Collections.singletonList(jwtAuthenticationProvider));
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.disable()); /* memo : csrf 토큰을 쓰지 않겠다.*/

        /* 설명, 허용되는 경로 및 권한 설정 : 화이트 라벨? */
        http.authorizeHttpRequests(authz ->
                        authz.requestMatchers(new AntPathRequestMatcher("/users/**", "GET")).hasRole("ADMIN") /* 필터링*/
                                .anyRequest().authenticated() /* memo : 나머지는 인증이 되어있는 사람만  */
                )
                .authenticationManager(authenticationManager())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilter(getAuthenticationFilter(authenticationManager()));

        return http.build();
    }


    /* 설명. Filter는 jakarta.servlet으로 import */
    private Filter getAuthenticationFilter(AuthenticationManager authenticationManger) {
        return new AuthenticationFilter(authenticationManger);
    }
}
