package org.biz.shopverse.mapper.member;

import org.biz.shopverse.dto.auth.MemberWithRoles;
import org.biz.shopverse.dto.member.response.MemberResponse;

public interface MemberMapper {
    MemberResponse findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    MemberWithRoles findByMemberWithRoles(String loginId);
} 