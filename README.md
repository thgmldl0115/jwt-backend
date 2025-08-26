

# Spring Security + JWT 발급 연습

2025.08.26

---

## 📌 Backend (Spring Boot)

### 프로젝트 설정
1. Spring Starter Project 생성
2. Dependencies:  
   - Lombok  
   - Validation  
   - Spring Data JPA  
   - H2 Database  
   - Spring Security  
   - Spring Web
3. `build.gradle`에 JWT 의존성 추가
4. `application.properties`에 서버포트, H2 설정, JPA 설정, SQL 초기화, JSON 직렬화, JWT 설정

---

### 패키지 구조 및 파일

**`com.example.demo.user`**
- `Role` Enum 생성 (ROLE_USER, ROLE_ADMIN)
- `User` 엔티티 생성
- `UserRepository` 생성

**`com.example.demo.config`**
- `AppInitConfig` : 앱 시작 시 기본 계정(admin, user) 주입
- `SecurityConfig` : Spring Security + JWT 설정

**`com.example.demo.auth.jwt`**
- `JwtProvider` : 토큰 발급/검증
- `JwtAuthenticationFilter` : 토큰 검증 필터

**`com.example.demo.auth.dto`**
- `LoginRequest` : 로그인 요청 DTO
- `AuthResponse` : 로그인 응답 DTO

**`com.example.demo.auth`**
- `AuthController` : 로그인/내 정보 확인 API

---

### JWT 키 생성 (PowerShell)
```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object {Get-Random -Maximum 256}))
````

예시:

```
LdB9l6etB6jp0G0RSUvOJ1XdyA35XG2OK+yoLZKkfS8=
```

---

### application.properties

```properties
jwt.secret=VGhpcy1pcy1hLXN1cGVyLXNlY3JldC1rZXktMzJieXRlcw==
jwt.expires-minutes=30
```

---

### Postman 확인

* `POST http://localhost:8081/auth/login` → 로그인 후 토큰 발급
* `GET  http://localhost:8081/auth/me` → 200 응답 확인

---

### Security 인가 규칙

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/auth/login").permitAll()
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers("/user/**").hasAnyRole("USER","ADMIN")
    .anyRequest().authenticated()
)
```

**추가 컨트롤러**

* `AdminController` : 테스트용 (ADMIN 전용)
* `UserController` : 테스트용 (USER/ADMIN 전용)

**기타**

* `WebConfig` : 전역 CORS 설정 클래스

---

## 📌 Frontend (React)

### 프로젝트 생성

```bash
npx create-react-app frontend
npm i axios react-router-dom zustand jwt-decode
```

### 폴더 구조

```
src/
  pages/
    LoginPage.jsx
    UserPage.jsx
    AdminPage.jsx
  store/
    useAuthStore.js
  App.jsx
  index.js
  Layout.jsx
  UnauthorizedPage.jsx
```

### 파일 설명

* **`useAuthStore.js`** : 로그인/로그아웃 상태 관리 (토큰 저장/삭제)
* **`LoginPage.jsx`** : 로그인 폼, 로그인 성공 시 권한별 페이지 이동
* **`UserPage.jsx` / `AdminPage.jsx`** : 권한별 전용 화면
* **`UnauthorizedPage.jsx`** : 권한 없는 사용자 접근 시 안내
* **`Layout.jsx`** : 전체 레이아웃 (좌측 메뉴 + 메인 + 하단 상태 콘솔)
* **`App.jsx`** : 라우팅 & 권한 분기 총괄
* **`index.js`** : React 앱 시작점

---

## ✅ 테스트 흐름

1. Postman으로 관리자 로그인 → JWT 토큰 발급
2. `/admin/ping` 접근 시 토큰 포함 → 정상 응답 확인
3. 프론트엔드 로그인 → 토큰 저장 → 권한에 따라 다른 페이지 접근


