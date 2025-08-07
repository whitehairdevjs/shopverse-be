package org.biz.shopverse.dto.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               // 1) @Getter, @Setter, toString(), equals(), hashCode() + RequiredArgsConstructor 생성
@NoArgsConstructor  // 2) Jackson이 객체 생성 시 사용할 기본 생성자 추가
@AllArgsConstructor // 3) 모든 필드를 인자로 받는 생성자 추가 (테스트나 매뉴얼 생성 시 유용)
@Schema(description = "회원 로그인 요청 DTO")
public class MemberLoginRequest {
    
    @Schema(description = "로그인 ID (이메일 또는 사용자명)", example = "user", required = true)
    @NotBlank(message = "로그인 ID는 필수입니다")
    private String loginId;
    
    @Schema(description = "비밀번호", example = "password123!", required = true)
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
} 