package org.biz.shopverse.dto.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 비밀번호 변경 요청 DTO")
public class MemberPasswordChangeRequest {
    
    @Schema(description = "현재 비밀번호", example = "currentPassword123!", required = true)
    @NotBlank(message = "현재 비밀번호는 필수입니다")
    private String currentPassword;
    
    @Schema(description = "새 비밀번호", example = "newPassword123!", required = true)
    @NotBlank(message = "새 비밀번호는 필수입니다")
    @Size(min = 8, max = 100, message = "새 비밀번호는 8자 이상 100자 이하여야 합니다")
    private String newPassword;
    
    @Schema(description = "새 비밀번호 확인", example = "newPassword123!", required = true)
    @NotBlank(message = "새 비밀번호 확인은 필수입니다")
    private String confirmPassword;
} 