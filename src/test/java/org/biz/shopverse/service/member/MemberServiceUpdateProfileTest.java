package org.biz.shopverse.service.member;

import org.biz.shopverse.dto.member.request.MemberUpdateRequest;
import org.biz.shopverse.dto.member.response.MemberResponse;
import org.biz.shopverse.dto.common.ApiResponse;
import org.biz.shopverse.mapper.member.MemberMapper;
import org.biz.shopverse.security.JwtTokenProvider;
import org.biz.shopverse.service.auth.JwtTokenRedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceUpdateProfileTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtTokenRedisService jwtTokenRedisService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private MemberService memberService;

    private MemberUpdateRequest memberUpdateRequest;
    private MemberResponse currentMember;
    private MemberResponse updatedMember;

    @BeforeEach
    void setUp() {
        memberUpdateRequest = MemberUpdateRequest.builder()
                .name("김철수")
                .nickname("철수")
                .phone("010-1234-5678")
                .email("kim@example.com")
                .gender("M")
                .marketingYn(true)
                .smsYn(false)
                .emailYn(true)
                .build();

        currentMember = MemberResponse.builder()
                .id(1L)
                .loginId("testuser")
                .name("홍길동")
                .nickname("길동이")
                .phone("010-9876-5432")
                .email("hong@example.com")
                .gender("M")
                .build();

        updatedMember = MemberResponse.builder()
                .id(1L)
                .loginId("testuser")
                .name("김철수")
                .nickname("철수")
                .phone("010-1234-5678")
                .email("kim@example.com")
                .gender("M")
                .build();
    }

    @Test
    void updateProfile_Success() {
        // Given
        String accessToken = "valid.access.token";
        String bearerToken = "Bearer " + accessToken;
        String loginId = "testuser";
        String role = "USER";

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenProvider.isTokenValid(accessToken)).thenReturn(true);
        when(jwtTokenProvider.getUserId(accessToken)).thenReturn(loginId);
        when(jwtTokenProvider.getUserRole(accessToken)).thenReturn(role);
        when(memberMapper.findByLoginId(loginId)).thenReturn(currentMember);
        when(memberMapper.existsByEmail(memberUpdateRequest.getEmail())).thenReturn(false);
        when(memberMapper.updateMember(eq(loginId), any(MemberUpdateRequest.class))).thenReturn(1);

        // When
        ResponseEntity<ApiResponse<MemberResponse>> response = memberService.updateProfile(request, memberUpdateRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("개인정보가 성공적으로 수정되었습니다.", response.getBody().getMessage());
        
        MemberResponse result = response.getBody().getData();
        assertEquals("김철수", result.getName());
        assertEquals("철수", result.getNickname());
        assertEquals("010-1234-5678", result.getPhone());
        assertEquals("kim@example.com", result.getEmail());
        assertEquals("M", result.getGender());
        assertEquals(role, result.getRole());

        verify(memberMapper).updateMember(eq(loginId), any(MemberUpdateRequest.class));
    }

    @Test
    void updateProfile_NoAuthorizationHeader() {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        ResponseEntity<ApiResponse<MemberResponse>> response = memberService.updateProfile(request, memberUpdateRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("인증 실패", response.getBody().getError());
        assertEquals("액세스 토큰이 제공되지 않았습니다.", response.getBody().getMessage());
    }

    @Test
    void updateProfile_InvalidToken() {
        // Given
        String accessToken = "invalid.access.token";
        String bearerToken = "Bearer " + accessToken;

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenProvider.isTokenValid(accessToken)).thenReturn(false);

        // When
        ResponseEntity<ApiResponse<MemberResponse>> response = memberService.updateProfile(request, memberUpdateRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("인증 실패", response.getBody().getError());
        assertEquals("유효하지 않은 토큰입니다.", response.getBody().getMessage());
    }

    @Test
    void updateProfile_EmailAlreadyExists() {
        // Given
        String accessToken = "valid.access.token";
        String bearerToken = "Bearer " + accessToken;
        String loginId = "testuser";

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenProvider.isTokenValid(accessToken)).thenReturn(true);
        when(jwtTokenProvider.getUserId(accessToken)).thenReturn(loginId);
        when(memberMapper.findByLoginId(loginId)).thenReturn(currentMember);
        when(memberMapper.existsByEmail(memberUpdateRequest.getEmail())).thenReturn(true);

        // When
        ResponseEntity<ApiResponse<MemberResponse>> response = memberService.updateProfile(request, memberUpdateRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("이메일 중복", response.getBody().getError());
        assertEquals("이미 사용중인 이메일입니다.", response.getBody().getMessage());
    }

    @Test
    void updateProfile_UpdateFailed() {
        // Given
        String accessToken = "valid.access.token";
        String bearerToken = "Bearer " + accessToken;
        String loginId = "testuser";

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenProvider.isTokenValid(accessToken)).thenReturn(true);
        when(jwtTokenProvider.getUserId(accessToken)).thenReturn(loginId);
        when(memberMapper.findByLoginId(loginId)).thenReturn(currentMember);
        when(memberMapper.existsByEmail(memberUpdateRequest.getEmail())).thenReturn(false);
        when(memberMapper.updateMember(eq(loginId), any(MemberUpdateRequest.class))).thenReturn(0);

        // When
        ResponseEntity<ApiResponse<MemberResponse>> response = memberService.updateProfile(request, memberUpdateRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("수정 실패", response.getBody().getError());
        assertEquals("개인정보 수정에 실패했습니다.", response.getBody().getMessage());
    }
}
