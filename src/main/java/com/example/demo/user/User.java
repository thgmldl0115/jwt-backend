package com.example.demo.user;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

/**
 * 사용자 엔티티
 * - 아이디(userId), 비밀번호(userPw), 이름(userNm), 권한(roles)
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(nullable = false, unique = true, length = 50)
    private String userId;

    @Column(nullable = false, length = 200)
    private String userPw; 
    
    @Column(nullable = false, length = 100)
    private String userNm;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<Role> roles;       // 권한 목록 (ROLE_USER, ROLE_ADMIN)
}
