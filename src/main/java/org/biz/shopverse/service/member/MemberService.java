package org.biz.shopverse.service.member;

import org.biz.shopverse.domain.member.Member;
import org.biz.shopverse.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;

    public Member findByLoginId(String loginId) {
        return memberMapper.findByLoginId(loginId);
    }
} 