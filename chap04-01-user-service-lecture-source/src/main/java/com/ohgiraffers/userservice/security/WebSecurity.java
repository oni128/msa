package com.ohgiraffers.userservice.security;

import jakarta.servlet.Filter;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collections;

/* 설명. WebSecurityConfigurerAdapter를 상속 받거나 @EnableWebSecurity를 쓰는 예제는 옛날 방식 */
@Configuration
public class WebSecurity{

    private JwtAuthenticationProvider jwtAuthenticationProvider;
    private Environment env;                // 의존성 주입 DI
    private JwtUtil jwtUtil;

    @Autowired
    public WebSecurity(JwtAuthenticationProvider jwtAuthenticationProvider, Environment env, JwtUtil jwtUtil) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.env = env;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(jwtAuthenticationProvider));
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception{
        http.csrf(csrf -> csrf.disable());/* memo : csrf 토큰을 쓰지 않겠다.*/

        /* 설명. 허용되는 경로 및 권한 설정 */
        http.authorizeHttpRequests(authz ->
//                        authz.requestMatchers(new AntPathRequestMatcher("/users/**")).permitAll()
//                                .anyRequest().authenticated()/* memo : 나머지는 인증이 되어있는 사람만  */

                                authz.requestMatchers(new AntPathRequestMatcher("/health", "GET")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/users/**" , "POST")).permitAll()
                                        .requestMatchers(new AntPathRequestMatcher("/users/**","GET")).hasRole("ENTERPRICE")
                                        .anyRequest().authenticated()

                )
                .authenticationManager(authenticationManager())
                /* 설명. Session을 쓰지 않고 Jwt토큰 방식으로 LocalThread에 저장하겠다는 설정 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilter(getAuthenticationFilter(authenticationManager()));

        /* 설명. 로그인 이후 사용자가 들고 온 (request header에 발급받은 bearer 토큰을 들고 옴) 토큰을 검증하기 위한 필터 */
        http.addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /* 설명. Filter는 jakarta.servlet으로 import */
    private Filter getAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new AuthenticationFilter(authenticationManager, env);
    }
}