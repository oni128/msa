package com.ohgiraffers.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.userservice.vo.RequestLoginVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/*
 * 설명. JWT(Json Web Token)의 구조
 *
 * 설명.
 *  1. 헤더(Header)
 *    - typ: 토큰의 타입 지정(JWT)
 *    - alg: 해싱 알고리즘으로 Verify Signature에서 사용 됨
 * 설명.
 *  2. 내용 또는 정보(Payload)
 *    - 토큰에 담을 정보가 들어 있음
 *    - 담는 정보의 한 조각을 클레임(claim - name과 value의 쌍으로 구성)이라 부름
 *       a. 등록된 클레임(registered claim)
 *          : 토큰에 대한 정보가 담김
 *            (iss: 토큰 발급자(issuer)
 *             sub: 토큰 제목(subject)
 *             aud: 토큰 대상자(audience)
 *             exp: 토큰의 만료 시간(expiration)
 *             nbf: 토큰 활성화(발급) 날짜(not before)
 *             iat: 토큰 활성화(발급) 시간(issued at))
 * 설명.
 *       b. 공개 클레임(public claim)
 *       	: 사용자 정의 클레임으로 공개용 정보를 위해 사용(충돌 방지를 위해 URI로 구성)
 * 설명.
 *       c. 비공개 클레임(private claim)
 *      	: 사용자 정의 클레임으로 서버(JWT 발급자)와 클라이언트 사이에 임의로 지정한 정보를 저장
 *            (충돌 발생 우려가 있어 조심해서 사용할 것)
 * 설명.
 *  3. 서명(Verify Signature)
 *    - Header 인코딩 값과 Payload 인코딩 값을 합쳐서 비밀 키로 해쉬(헤더의 해싱 알고리즘으로)하여 생성
 */
@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    /* memo : manager가 뭐인지 지정해주는 ?*/
    private Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager, Environment env) {
        super(authenticationManager);
        this.env = env;
    }

    /* 설명. 로그인 시도 시 동작하는 기능(POST /login 요청 시) */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {

            /* 설명. request를 통해 넘어온 json(login 시 id/pwd)를 RequestLoginVO 옮겨 담기 */
            /* memo : objectMapper를 이용하여 원하는 타입으로 파싱? 가능 */
            /* memo : request가 json형태로 넘어오기 때문에 stream을 이용하여 파싱? */
            RequestLoginVO creds = new ObjectMapper().readValue(request.getInputStream(), RequestLoginVO.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPwd(), new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* 설명. 로그인 성공 시(Authentication authResult에는 로그인에 성공한 User 객체가 들어있음) JWT 토큰 생성할 예정 */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 이후 spring security가 Authentication 객체로 관리 되어 넘어옴: {}", authResult);

        /* 설명. 로그인 이후 넘어온 Authentication 객체를 활용해 JWT 토큰 만들기 */
        log.info("시크릿 키: {}", env.getProperty("token.secret"));   // 토큰 생성을 위한 secret key

        /* 설명. 토큰의 payload에 (id, 가진 권한들, 만료시간)을 담을 예정 */
        String id = ((User)authResult.getPrincipal()).getUsername();
        log.info("회원의 아이디(이메일): {}", id);

        List<String> roles = authResult.getAuthorities().stream()
//                                       .map(role -> role.getAuthority())
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        log.info("List<String> 형태로 뽑아낸 로그인 한 회원의 권한들: {}", roles);
        log.info("만료 시간: {}", env.getProperty("token.expiration_time"));

        /* 설명. 이제부터 위에서 추출한 재료들로 JWT Token 제작(JWT 라이브러리 추가) */
        Claims claims = Jwts.claims().setSubject(id);
        claims.put("auth", roles);

        String token = Jwts.builder()
//                .setSubject(id)
                .setClaims(claims)      // 등록된 클레임(subject) + 비공개 클레임(auth)
                .setExpiration(new Date(System.currentTimeMillis()
                        + Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();

        response.addHeader("token", token);
    }
}