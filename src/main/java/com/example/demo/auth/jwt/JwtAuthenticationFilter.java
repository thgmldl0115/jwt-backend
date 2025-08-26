package com.example.demo.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {  // 요청 1건당 한번만 실행됨

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException { 

        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {  // Authorization 헤더에서 Bearer 토큰을 꺼내 검증. 헤더가 없거나 Bearer 로 시작하지 않으면 아무것도 하지 않음 (익명 사용자 취급)
            String token = auth.substring(7);
            try {
                Jws<Claims> jws = jwtProvider.parse(token);  // 서명, 형식, 만료 검증 
                Claims claims = jws.getBody();

                String userId = claims.getSubject();		// claims 에서 로그인 ID 추출
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) claims.get("roles"); // claims 에서 Role 문자열 리스트 추출

                Collection<SimpleGrantedAuthority> authorities =
                        roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());  // 권한 컬렉션 생성
                
                System.out.println(">>> JWT authorities = " + authorities);

                // 비밀번호(null) 대신 인증된 사용자 토큰 생성해서 컨텍스트에 주입
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);  // 파라미터(자격증명)는 JWT 흐름에선 의미 없어 null
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // 토큰이 잘못되었으면 인증 컨텍스트 비우고 그대로 진행(인가 단계에서 401/403 처리)
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
