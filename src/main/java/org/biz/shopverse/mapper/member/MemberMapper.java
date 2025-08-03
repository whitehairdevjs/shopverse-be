package org.biz.shopverse.mapper.member;

import org.biz.shopverse.domain.member.Member;

public interface MemberMapper {
    Member findByLoginId(String loginId);
} 