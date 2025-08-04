package org.biz.shopverse.service.member;

import org.biz.shopverse.dto.auth.MemberWithRoles;
import org.biz.shopverse.dto.member.response.MemberResponse;
import org.biz.shopverse.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;

    public MemberResponse findByLoginId(String loginId) {
        return memberMapper.findByLoginId(loginId);
    }

    public boolean existsByLoginId(String loginId) {
        return memberMapper.existsByLoginId(loginId);
    }

    public MemberWithRoles findByMemberWithRoles(String loginId) {
        return memberMapper.findByMemberWithRoles(loginId);
    }
} 