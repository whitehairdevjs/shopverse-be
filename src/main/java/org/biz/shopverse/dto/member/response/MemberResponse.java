package org.biz.shopverse.dto.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 정보 응답 DTO")
public class MemberResponse {
    
    @Schema(description = "회원 ID", example = "1")
    private Long id;
    
    @Schema(description = "로그인 ID", example = "user")
    private String loginId;
    
    @Schema(description = "이름", example = "홍길동")
    private String name;
    
    @Schema(description = "닉네임", example = "길동이")
    private String nickname;
    
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    
    @Schema(description = "이메일", example = "user@example.com")
    private String email;
    
    @Schema(description = "성별 (M: 남성, F: 여성, U: 미설정)", example = "M")
    private String gender;
    
    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthDate;
    
    @Schema(description = "소셜 로그인 여부", example = "false")
    private Boolean isSocial;
    
    @Schema(description = "소셜 제공자", example = "kakao")
    private String socialProvider;
    
    @Schema(description = "마케팅 수신 동의", example = "false")
    private Boolean marketingYn;
    
    @Schema(description = "문자 수신 동의", example = "true")
    private Boolean smsYn;
    
    @Schema(description = "이메일 수신 동의", example = "true")
    private Boolean emailYn;
    
    @Schema(description = "적립 포인트", example = "1000")
    private Integer point;
    
    @Schema(description = "회원 상태", example = "ACTIVE")
    private String status;
    
    @Schema(description = "회원 역할", example = "USER")
    private String role;
    
    @Schema(description = "마지막 로그인 시각", example = "2024-01-01T10:00:00")
    private String lastLoginAt;
    
    @Schema(description = "생성 시각", example = "2024-01-01T10:00:00")
    private String createdAt;
    
    @Schema(description = "수정 시각", example = "2024-01-01T10:00:00")
    private String updatedAt;
} 