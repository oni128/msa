package com.ohgiraffers.userservice.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
/*@AutoWired뭐차이 */
    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        log.info("들고 온 토큰 확인: {}", authorizationHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            /*memo : 토큰은 잇지만 비어있지 않다면 Bearer로 시작하는게 맞닝*/
            String token = authorizationHeader.substring(7); // "Bearer "를 제외한 토큰 부분만 추출
            log.info("순수 토큰 값: " + token);
            if (jwtUtil.validateToken(token)) {

                /* 설명. 유효한 토큰을 통해 아이디와 권한들을 가진 Authentication 추출 (Spring Security가 인식할 수 있게 반환)*/
                Authentication authentication = jwtUtil.getAuthentication(token);

                /* memo: 이제 인식만 시키면됨 - Authentication에 넣기만하면됨. */
                /* 설명. Spring Security가 인식할 수 있게 주입(LocalThread에 저장)*(요청당  */
                /* memo: 서버의 부담이 줄어듬*/
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }


        }
        /* 설명. 다음 필터를 진행하게 해 줘야 AuthenticationFilter가 동작함 */
        filterChain.doFilter(request, response);
    }
}

