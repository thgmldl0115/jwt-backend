package com.example.demo.auth.jwt;

import com.example.demo.user.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    private final Key key;
    private final long expiresMinutes;

    public JwtProvider(
            @Value("${jwt.secret}") String base64Secret,
            @Value("${jwt.expires-minutes}") long expiresMinutes  // 만료시간 주입
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.expiresMinutes = expiresMinutes;
    }

    public String generateToken(String userId, Collection<Role> roles, String userNm) {  // 로그인 성공시 JWT 토큰 생성 
        Instant now = Instant.now();
        Date issuedAt = Date.from(now); // 토큰 발급 시각
        Date expiry = Date.from(now.plusSeconds(expiresMinutes * 60)); // 토큰 만료 시각

        List<String> roleNames = roles.stream()
                .map(Enum::name)
                .collect(Collectors.toList());  // Role 문자열 리스트로 변환

        return Jwts.builder()
                .setSubject(userId)			 // 토큰 주제: userId
                .claim("roles", roleNames)   // roles 배열 claim 추가
                .claim("userNm", userNm)     // 사용자 이름 claim 추가
                .setIssuedAt(issuedAt)		 // 발급 시각
                .setExpiration(expiry)		 // 만료 시각
                .signWith(key, SignatureAlgorithm.HS256)  // 서명
                .compact(); 
    }

    public Jws<Claims> parse(String token) {  // 클라이언트가 보낸 토큰을 파싱(검증)하는 메서드.
        return Jwts.parserBuilder()
                .setSigningKey(key)   		// 검증에 쓸 서명키 등록
                .build()					// 설정 확정 후 JwtParser 생성
                .parseClaimsJws(token);		// 토큰 검증(구조, 서명, 만료 등) 및 Claims 추출
    }
}
