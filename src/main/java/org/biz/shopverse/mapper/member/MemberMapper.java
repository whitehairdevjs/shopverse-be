package org.biz.shopverse.mapper.member;

import org.biz.shopverse.dto.member.MemberWithRoles;
import org.biz.shopverse.dto.member.request.MemberCreateRequest;
import org.biz.shopverse.dto.member.response.MemberResponse;

public interface MemberMapper {
    MemberResponse findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    MemberWithRoles findByMemberWithRoles(String loginId);

    int createMember(MemberCreateRequest memberCreateRequest);
} 