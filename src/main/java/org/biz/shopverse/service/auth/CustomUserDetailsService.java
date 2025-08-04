package org.biz.shopverse.service.auth;

import org.biz.shopverse.dto.auth.MemberWithRoles;
import org.biz.shopverse.dto.member.response.MemberResponse;
import org.biz.shopverse.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service 레이어:
 * MyBatis 매퍼를 통해 DB에서 User + Roles 조회 → Spring Security 에 UserDetails 로 전달
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        MemberWithRoles resUser = memberService.findByMemberWithRoles(loginId);

        if (resUser == null) {
            throw new UsernameNotFoundException("User not found: " + loginId);
        }
        // DB에서 가져온 role 문자열을 GrantedAuthority 리스트로 변환
        List<GrantedAuthority> auths = resUser.getRoles()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return User.withUsername(resUser.getLoginId())
                .password(resUser.getPassword())
                .authorities(auths)
                .build();
    }
}
