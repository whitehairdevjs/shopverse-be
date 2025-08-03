package org.biz.shopverse.domain.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 정보")
public class Member {
    
    @Schema(description = "회원 고유 ID", example = "1")
    private Long id;
    
    @Schema(description = "로그인 ID (이메일 또는 사용자명)", example = "user@example.com", required = true)
    private String loginId;
    
    @Schema(description = "암호화된 비밀번호", example = "$2a$10$encrypted.password.hash")
    private String password;
    
    @Schema(description = "회원 이름", example = "홍길동", required = true)
    private String name;
    
    @Schema(description = "닉네임", example = "길동이")
    private String nickname;
    
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    
    @Schema(description = "이메일 주소", example = "user@example.com")
    private String email;
    
    @Schema(description = "성별 (M: 남성, F: 여성, U: 미설정)", example = "M", allowableValues = {"M", "F", "U"})
    private String gender;
    
    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthDate;
    
    @Schema(description = "소셜 로그인 여부", example = "false")
    private Boolean isSocial;
    
    @Schema(description = "소셜 로그인 제공자 (kakao, naver, google 등)", example = "kakao")
    private String socialProvider;
    
    @Schema(description = "회원 권한 (USER, SELLER, ADMIN)", example = "USER", allowableValues = {"USER", "SELLER", "ADMIN"})
    private String role;
    
    @Schema(description = "마케팅 수신 동의 여부", example = "true")
    private Boolean marketingYn;
    
    @Schema(description = "SMS 수신 동의 여부", example = "true")
    private Boolean smsYn;
    
    @Schema(description = "이메일 수신 동의 여부", example = "true")
    private Boolean emailYn;
    
    @Schema(description = "적립 포인트", example = "1000")
    private Integer point;
    
    @Schema(description = "회원 상태 (ACTIVE, INACTIVE, DELETED)", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "DELETED"})
    private String status;
    
    @Schema(description = "마지막 로그인 시각", example = "2024-01-15T10:30:00")
    private LocalDateTime lastLoginAt;
    
    @Schema(description = "회원가입 시각", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "정보 수정 시각", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
} 