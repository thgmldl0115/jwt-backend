package com.example.demo.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * users 테이블 접근용 리포지토리
 * - PK가 String(userId)이므로 <User, String>
 */
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
