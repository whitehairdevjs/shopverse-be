package org.biz.shopverse.service.member;

import org.biz.shopverse.dto.auth.MemberWithRoles;
import org.biz.shopverse.dto.member.request.MemberCreateRequest;
import org.biz.shopverse.dto.member.response.MemberResponse;
import org.biz.shopverse.exception.CustomBusinessException;
import org.biz.shopverse.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

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