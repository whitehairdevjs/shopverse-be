package org.biz.shopverse.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               // 1) @Getter, @Setter, toString(), equals(), hashCode() + RequiredArgsConstructor 생성
@NoArgsConstructor  // 2) Jackson이 객체 생성 시 사용할 기본 생성자 추가
@AllArgsConstructor // 3) 모든 필드를 인자로 받는 생성자 추가 (테스트나 매뉴얼 생성 시 유용)
public class LoginRequest {
    private String userId;
    private String password;
}