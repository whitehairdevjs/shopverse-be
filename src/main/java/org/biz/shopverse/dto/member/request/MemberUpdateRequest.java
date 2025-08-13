package org.biz.shopverse.dto.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 개인정보 수정 요청")
public class MemberUpdateRequest {
    
    @Schema(description = "이름", example = "홍길동")
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
    
    @Schema(description = "닉네임", example = "길동이")
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;
    
    @Schema(description = "전화번호", example = "01012345678")
    @Pattern(regexp = "^01[0-9][0-9]{3,4}[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다.")
    private String phone;
    
    @Schema(description = "이메일", example = "hong@example.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    
    @Schema(description = "성별", example = "M", allowableValues = {"M", "F"})
    @Pattern(regexp = "^(M|F)$", message = "성별은 M 또는 F만 입력 가능합니다.")
    private String gender;
    
    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthDate;
    
    @Schema(description = "마케팅 수신 동의 여부", example = "true")
    private Boolean marketingYn;
    
    @Schema(description = "SMS 수신 동의 여부", example = "true")
    private Boolean smsYn;
    
    @Schema(description = "이메일 수신 동의 여부", example = "false")
    private Boolean emailYn;
}
