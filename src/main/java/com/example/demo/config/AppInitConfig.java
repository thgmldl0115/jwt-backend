package com.example.demo.config;

import com.example.demo.user.Role;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppInitConfig {

    // 비밀번호 해시용 BCrypt 인코더 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 앱 시작 시 기본 계정(admin/user) 주입
    @Bean
    public org.springframework.boot.CommandLineRunner dataSeeder(
            UserRepository userRepository,
            PasswordEncoder encoder
    ) {
        return args -> {
            if (!userRepository.existsByUserId("admin")) {
                userRepository.save(User.builder()
                        .userId("admin")
                        .userPw(encoder.encode("admin1234"))
                        .userNm("관리자")
                        .roles(Set.of(Role.ROLE_ADMIN))
                        .build());
            }
            if (!userRepository.existsByUserId("user")) {
                userRepository.save(User.builder()
                        .userId("user")
                        .userPw(encoder.encode("user1234"))
                        .userNm("일반사용자")
                        .roles(Set.of(Role.ROLE_USER))
                        .build());
            }
        };
    }
}
