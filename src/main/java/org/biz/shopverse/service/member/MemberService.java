package org.biz.shopverse.service.member;

import org.biz.shopverse.dto.auth.TokenResponse;
import org.biz.shopverse.dto.member.MemberWithRoles;
import org.biz.shopverse.dto.member.request.MemberCreateRequest;
import org.biz.shopverse.dto.member.request.MemberLoginRequest;
import org.biz.shopverse.dto.member.response.MemberResponse;
import org.biz.shopverse.exception.CustomBusinessException;
import org.biz.shopverse.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.biz.shopverse.security.JwtTokenProvider;
import org.biz.shopverse.service.auth.JwtTokenRedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenRedisService jwtTokenRedisService;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenValidityInMs;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidityInMs;

    public MemberResponse findByLoginId(String loginId) {
        return memberMapper.findByLoginId(loginId);
    }

    public boolean existsByLoginId(String loginId) {
        return memberMapper.existsByLoginId(loginId);
    }

    public boolean existsByEmail(String email) {
        return memberMapper.existsByEmail(email);
    }

    public MemberWithRoles findByMemberWithRoles(String loginId) {
        return memberMapper.findByMemberWithRoles(loginId);
    }

    public int createMember(MemberCreateRequest memberCreateRequest) {
        try {
            return memberMapper.createMember(memberCreateRequest);
        } catch (Exception e) {
            throw new CustomBusinessException("회원가입 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<TokenResponse> login(MemberLoginRequest memberLoginRequest) {
        MemberWithRoles memberWithRoles = findByMemberWithRoles(memberLoginRequest.getLoginId());

        if (memberWithRoles == null) {
            throw new CustomBusinessException("권한, 정보가 존재하지 않는 사용자입니다.");
        }

        if (!passwordEncoder.matches(memberLoginRequest.getPassword(), memberWithRoles.getPassword())) {
            throw new CustomBusinessException("잘못된 사용자 정보를 입력 하셨습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(memberWithRoles.getLoginId(), memberWithRoles.getRoles(), accessTokenValidityInMs);
        String refreshToken = jwtTokenProvider.generateRefreshToken(memberWithRoles.getLoginId(), refreshTokenValidityInMs);

        jwtTokenRedisService.saveRefreshToken(memberWithRoles.getLoginId(), refreshToken, refreshTokenValidityInMs);  // 7일

        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
    }

    public boolean signup(MemberCreateRequest memberCreateRequest) {
        if (existsByLoginId(memberCreateRequest.getLoginId())) {
            throw new CustomBusinessException("이미 사용중인 ID 입니다.", HttpStatus.CONFLICT);
        }

        if (existsByEmail(memberCreateRequest.getEmail())) {
            throw new CustomBusinessException("이미 사용중인 이메일 입니다.", HttpStatus.CONFLICT);
        }

        String encodedPassword = passwordEncoder.encode(memberCreateRequest.getPassword());
        memberCreateRequest.setPassword(encodedPassword);

        return createMember(memberCreateRequest) > 0;
    }
} 