package org.biz.shopverse.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.biz.shopverse.domain.member.Member;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private Member member;
    private String accessToken;
    private String refreshToken;
}