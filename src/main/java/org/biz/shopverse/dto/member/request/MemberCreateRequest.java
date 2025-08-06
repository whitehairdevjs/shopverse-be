package org.biz.shopverse.dto.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 가입 요청 DTO")
public class MemberCreateRequest {
    
    @Schema(description = "로그인 ID (이메일 또는 사용자명)", example = "user@example.com", required = true)
    @NotBlank(message = "로그인 ID는 필수입니다")
    @Size(min = 5, max = 50, message = "로그인 ID는 5자 이상 20자 이하여야 합니다")
    private String loginId;
    
    @Schema(description = "비밀번호", example = "password123!", required = true)
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$", 
             message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다")
    private String password;
    
    @Schema(description = "이름", example = "홍길동", required = true)
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 50, message = "이름은 50자 이하여야 합니다")
    private String name;
    
    @Schema(description = "닉네임", example = "길동이")
    @Size(max = 50, message = "닉네임은 50자 이하여야 합니다")
    private String nickname;
    
    @Schema(description = "전화번호", example = "010-1234-5678")
    @Pattern(regexp = "^[0-9-]+$", message = "전화번호 형식이 올바르지 않습니다")
    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다")
    private String phone;
    
    @Schema(description = "이메일", example = "user@example.com")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
    private String email;
    
    @Schema(description = "성별 (M: 남성, F: 여성, U: 미설정)", example = "M", allowableValues = {"M", "F", "U"})
    @Pattern(regexp = "^[MFU]$", message = "성별은 M, F, U 중 하나여야 합니다")
    private String gender = "U";
    
    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthDate;
    
    @Schema(description = "소셜 로그인 여부", example = "false")
    private Boolean isSocial = false;
    
    @Schema(description = "소셜 제공자 (kakao, naver, google 등)", example = "kakao")
    @Size(max = 20, message = "소셜 제공자는 20자 이하여야 합니다")
    private String socialProvider;
    
    @Schema(description = "마케팅 수신 동의", example = "false")
    private Boolean marketingYn = false;
    
    @Schema(description = "문자 수신 동의", example = "true")
    private Boolean smsYn = true;
    
    @Schema(description = "이메일 수신 동의", example = "true")
    private Boolean emailYn = true;
} 