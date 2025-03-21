package com.ohgiraffers.userservice.security;

import com.ohgiraffers.userservice.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;

@Slf4j
@Component
public class JwtUtil {

    private final Key key;
    private final UserService userService;      //의존성 주입

    @Autowired
    public JwtUtil(
            @Value("${token.secret}") String secretKey,
            UserService userService
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userService = userService;
    }

    /* 설명. Token 검증(Bearer 토큰이 넘어왔고, 우리가 사이트의 secret key로 만들어 졌는지, 만료 되었는지, 내용이 비어있지 않은지) */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("유효하지 않은 JWT Token");
        } catch (ExpiredJwtException e) {
            log.info("만료기간이 지남");
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 JWT Token");
        } catch (IllegalArgumentException e) {
            log.info("토큰의 클레임이 비어있음");
        }
        return false;
    }

    /* 설명. 유효성 검증이 된 토큰에서 인증 객체를 반환 */
    public Authentication getAuthentication(String token) {

        Claims claims = parseClaims(token);

        /* 설명. 토큰에 들어있는 이메일로 DB에서 회원 조회하고 UserDetails로 가져옴 */
        UserDetails userDetails = userService.loadUserByUsername(claims.getSubject());

        /* 설명. 토큰에 들어있는 권한들을 List<GrantedAuthority> */
        Collection<GrantedAuthority> authorities = null;
        if (claims.get("auth") == null) {       // 권한이 없으면
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        } else {                                // 권한이 있으면
            authorities =
                    Arrays.stream(claims.get("auth").toString()
                            .replace("[", "")
                            .replace("]", "")
                            .split(", "))
                            .map(role -> new SimpleGrantedAuthority(role))
                            .collect(Collectors.toList());

        }
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    /* 설명. 토큰에서 payload에 담긴 클레임들만 추출 */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}