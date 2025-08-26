package com.example.demo.auth;

import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.jwt.JwtProvider;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;  	// DB에서 사용자 조회
    private final PasswordEncoder passwordEncoder; 	// 입력 비밀번호와 저장된 해시 비교
    private final JwtProvider jwtProvider;			// JWT 발급 및 검증	

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        User user = userRepository.findByUserId(req.getUserId())  // DB 에서 userId로 조회. 없으면 401
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getUserPw())) {  // 비밀번호 검증. 불일치시 401
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtProvider.generateToken( // JWT 토큰 발급
                user.getUserId(),
                user.getRoles(),
                user.getUserNm()
        );

        List<String> roleNames = user.getRoles().stream().map(Enum::name).toList();

        return new AuthResponse(
                "Bearer",
                token,
                60L * 30L, // 30분 만료
                new AuthResponse.UserInfo(user.getUserId(), user.getUserNm(), roleNames) // 로그인 성공 시 프론트에 이 JSON이 내려감.
        );
    }

    @GetMapping("/me")
    public AuthResponse.UserInfo me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");  // 인증정보가 없으면 401
        }
        String userId = (String) authentication.getPrincipal(); // 현재 사용자 ID 추출. 
        // JwtAuthenticationFilter에서 UsernamePasswordAuthenticationToken(userId, null, authorities) 로 세팅했기 때문에 principal = userId.

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")); // DB에서 사용자 정보 조회

        List<String> roleNames = user.getRoles().stream().map(Enum::name).toList(); // 권한 Enum을 문자열 리스트로 반환

        return new AuthResponse.UserInfo(user.getUserId(), user.getUserNm(), roleNames); // UserInfo DTO로 반환
    }
}
