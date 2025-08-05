package org.biz.shopverse.member;

import org.biz.shopverse.dto.member.request.MemberCreateRequest;
import org.biz.shopverse.dto.member.response.MemberResponse;
import org.biz.shopverse.mapper.member.MemberMapper;
import org.biz.shopverse.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberService memberService;

    @Test
    void findByLoginId() {
        String loginId = "jskim";

        // when
        memberService.findByLoginId(loginId);

        // then
        verify(memberMapper, times(1)).findByLoginId(loginId);
    }

    @Test
    void createMember_ShouldCallMapper() {
        // Given
        MemberCreateRequest request = new MemberCreateRequest();
        request.setLoginId("testuser");
        request.setPassword("encodedPassword");
        request.setName("테스트유저");
        request.setNickname("테스트");
        request.setPhone("010-1234-5678");
        request.setEmail("test@example.com");
        request.setGender("M");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setIsSocial(false);
        request.setMarketingYn(false);
        request.setSmsYn(true);
        request.setEmailYn(true);

        // When
        memberService.createMember(request);

        // Then
        verify(memberMapper, times(1)).createMember(request);
    }
} 