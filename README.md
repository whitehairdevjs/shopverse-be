# Shopverse Backend

Spring Boot 기반의 쇼핑몰 백엔드 API 서버입니다.

## 🛠 기술 스택

- **Framework**: Spring Boot 3.5.4
- **Language**: Java 17
- **Database**: PostgreSQL
- **ORM**: MyBatis
- **Security**: Spring Security + JWT
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Build Tool**: Gradle
- **Monitoring**: P6Spy (SQL 로깅)
- **Cache**: Redis

## 📁 프로젝트 구조

```
src/main/java/org/biz/shopverse/
├── ShopverseApplication.java          # 메인 애플리케이션
├── aspect/
│   └── PerformanceLoggingAspect.java # 성능 로깅 AOP
├── common/
│   └── util/
│       └── ResponseUtil.java         # 응답 유틸리티
├── config/
│   ├── SecurityConfig.java           # Spring Security 설정
│   ├── JwtAuthenticationEntryPoint.java
│   ├── OpenApiConfig.java
│   ├── RedisConfig.java
│   └── SchedulerConfig.java
├── controller/
│   ├── auth/
│   │   └── AuthController.java       # 인증 API
│   ├── user/
│   │   └── UserController.java       # 사용자 API
│   └── TestController.java           # 테스트 API
├── dto/
│   ├── auth/
│   │   ├── LoginRequest.java
│   │   ├── TokenRequest.java
│   │   └── TokenResponse.java
│   ├── error/
│   │   └── ErrorResponse.java
│   └── user/
│       ├── UserRequest.java
│       └── UserResponse.java
├── domain/
│   └── user/
│       └── DomainUser.java
├── exception/
│   └── auth/
│       ├── JwtInvalidException.java
│       └── JwtTokenExpiredException.java
├── mapper/
│   └── user/
│       └── UserMapper.java           # MyBatis 매퍼
├── scheduler/
│   └── TestSchedule.java
├── security/
│   ├── JwtFilter.java               # JWT 필터
│   └── JwtTokenProvider.java        # JWT 토큰 관리
└── service/
    ├── auth/
    │   ├── CustomUserDetailsService.java
    │   └── JwtTokenRedisService.java
    ├── user/
    │   └── UserService.java
    └── TestService.java
```

## 🚀 실행 방법

### 1. 환경 설정

```bash
# 환경 변수 설정
export PDB_KEY=your_postgres_password
export JWT_SECRET=your_jwt_secret_key
```

### 2. 데이터베이스 설정

PostgreSQL 데이터베이스가 필요합니다.

```sql
-- 데이터베이스 생성
CREATE DATABASE shopverse;

-- 사용자 테이블
CREATE TABLE user_info (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(50) UNIQUE NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_uuid VARCHAR(100),
    mobile_number VARCHAR(20),
    birth DATE,
    zipcode VARCHAR(10),
    address TEXT,
    address_detail TEXT
);

-- 역할 테이블
CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role_code VARCHAR(20) UNIQUE NOT NULL
);

-- 사용자-역할 매핑 테이블
CREATE TABLE user_roles (
    user_id VARCHAR(50) NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user_info(user_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);
```

### 3. 애플리케이션 실행

```bash
# Gradle로 실행
./gradlew bootRun

# 또는 JAR 파일로 실행
./gradlew build
java -jar build/libs/shopverse-be.jar
```

## 📚 API 문서

애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:

- **Swagger UI**: http://localhost:8081/docs/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/docs/v3/api-docs

## 🔐 인증 API

### 로그인
```http
POST /auth/login
Content-Type: application/json

{
  "userId": "user001",
  "password": "password123"
}
```

### 토큰 갱신
```http
POST /auth/reissue
Content-Type: application/json

{
  "refreshToken": "your_refresh_token"
}
```

## 👤 사용자 API

### 사용자 정보 조회
```http
POST /user/info
Authorization: Bearer your_access_token
Content-Type: application/json

{
  "userId": "user001"
}
```

## 🧪 테스트 API

### 헬로 월드
```http
GET /api/hello
```

### 리프레시 토큰 조회
```http
POST /api/getRtoken
Content-Type: application/json

{
  "userId": "user001"
}
```

### 사용자 역할 조회
```http
POST /api/get-role
Content-Type: application/json

{
  "refreshToken": "your_refresh_token"
}
```

## ⚙️ 설정

### application.properties 주요 설정

```properties
# 서버 포트
server.port=8081

# 데이터베이스 설정
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=${PDB_KEY}

# MyBatis 설정
mybatis.mapper-locations=classpath:mapper/**/*.xml
mybatis.type-aliases-package=org.biz.shopverse.domain
mybatis.configuration.map-underscore-to-camel-case=true

# JWT 설정
jwt.secret=${JWT_SECRET}
jwt.issuer=js-app
jwt.access-token-expiration=900000    # 15분
jwt.refresh-token-expiration=604800000 # 7일

# OpenAPI 설정
springdoc.api-docs.path=/docs/v3/api-docs
springdoc.swagger-ui.path=/docs/swagger-ui.html
```

## 🔧 개발 환경

### 필수 요구사항
- Java 17+
- Gradle 7.0+
- PostgreSQL 12+
- Redis (선택사항)

### IDE 설정
- IntelliJ IDEA 또는 Eclipse 권장
- Lombok 플러그인 설치 필요

## 📝 개발 가이드

### 새로운 API 추가
1. `controller` 패키지에 컨트롤러 클래스 생성
2. `dto` 패키지에 요청/응답 DTO 생성
3. `service` 패키지에 비즈니스 로직 구현
4. `mapper` 패키지에 MyBatis 매퍼 인터페이스 생성
5. `mapper` XML 파일에 SQL 쿼리 작성

### 보안 설정
- Spring Security를 사용한 JWT 기반 인증
- 역할 기반 접근 제어 (RBAC)
- CSRF 비활성화 (JWT 사용으로 인해)

### 로깅
- P6Spy를 통한 SQL 쿼리 로깅
- AOP를 통한 메서드 실행 시간 측정

## 🐛 문제 해결

### 일반적인 문제들

1. **데이터베이스 연결 실패**
   - PostgreSQL 서비스가 실행 중인지 확인
   - 데이터베이스 접속 정보 확인

2. **JWT 토큰 오류**
   - JWT_SECRET 환경 변수 설정 확인
   - 토큰 만료 시간 확인

3. **MyBatis 매퍼 오류**
   - XML 파일의 namespace 확인
   - resultType 패키지 경로 확인

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 👥 기여

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

**주의**: 이 프로젝트는 개발/학습 목적으로 제작되었습니다. 프로덕션 환경에서 사용하기 전에 보안 검토를 진행하시기 바랍니다. 