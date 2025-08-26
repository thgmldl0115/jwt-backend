package com.example.demo.config;

import com.example.demo.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.cors(cors -> {})
            .csrf(csrf -> csrf.disable())  // JWT 같은 토큰 기반 인증일 때는 굳이 CSRF 토큰 검사를 유지할 필요가 없음
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 경로별 접근 권한
            .authorizeHttpRequests(auth -> auth
        	    .requestMatchers("/auth/login").permitAll()
        	    .requestMatchers("/admin/**").hasRole("ADMIN")
        	    .requestMatchers("/user/**").hasAnyRole("USER","ADMIN")
        	    .anyRequest().authenticated()
        	)

            // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 컨트롤러가 실행될때 이미 인증정보가 들어있도록

        return http.build();
    }
}
