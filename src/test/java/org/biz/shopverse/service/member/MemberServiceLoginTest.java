package org.biz.shopverse.service.member;

import org.biz.shopverse.dto.auth.TokenResponse;
import org.biz.shopverse.dto.member.MemberWithRoles;
import org.biz.shopverse.dto.member.request.MemberLoginRequest;
import org.biz.shopverse.exception.CustomBusinessException;
import org.biz.shopverse.mapper.member.MemberMapper;
import org.biz.shopverse.security.JwtTokenProvider;
import org.biz.shopverse.service.auth.JwtTokenRedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceLoginTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtTokenRedisService jwtTokenRedisService;

    @InjectMocks
    private MemberService memberService;

    private MemberLoginRequest loginRequest;
    private MemberWithRoles memberWithRoles;

    @BeforeEach
    void setUp() {
        // JWT 토큰 만료 시간 설정
        ReflectionTestUtils.setField(memberService, "accessTokenValidityInMs", 3600000L); // 1시간
        ReflectionTestUtils.setField(memberService, "refreshTokenValidityInMs", 604800000L); // 7일

        // 테스트 데이터 설정
        loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId("testuser");
        loginRequest.setPassword("password123");

        memberWithRoles = new MemberWithRoles();
        memberWithRoles.setLoginId("testuser");
        memberWithRoles.setPassword("encodedPassword");
        memberWithRoles.setRoles("[\"ROLE_USER\"]"); // JSON 문자열 형태로 설정
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_Success() {
        // Given
        when(memberMapper.findByMemberWithRoles("testuser")).thenReturn(memberWithRoles);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(anyString(), anyList(), anyLong())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(anyString(), anyLong())).thenReturn("refresh-token");

        // When
        ResponseEntity<TokenResponse> response = memberService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("access-token", response.getBody().getAccessToken());
        assertEquals("refresh-token", response.getBody().getRefreshToken());

        verify(memberMapper, times(1)).findByMemberWithRoles("testuser");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
        verify(jwtTokenProvider, times(1)).generateAccessToken("testuser", Arrays.asList("ROLE_USER"), 3600000L);
        verify(jwtTokenProvider, times(1)).generateRefreshToken("testuser", 604800000L);
        verify(jwtTokenRedisService, times(1)).saveRefreshToken("testuser", "refresh-token", 604800000L);
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_Failure_UserNotFound() {
        // Given
        when(memberMapper.findByMemberWithRoles("nonexistent")).thenReturn(null);

        loginRequest.setLoginId("nonexistent");

        // When & Then
        CustomBusinessException exception = assertThrows(CustomBusinessException.class, () -> {
            memberService.login(loginRequest);
        });

        assertEquals("권한, 정보가 존재하지 않는 사용자입니다.", exception.getMessage());

        verify(memberMapper, times(1)).findByMemberWithRoles("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(anyString(), anyList(), anyLong());
        verify(jwtTokenProvider, never()).generateRefreshToken(anyString(), anyLong());
        verify(jwtTokenRedisService, never()).saveRefreshToken(anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_Failure_WrongPassword() {
        // Given
        when(memberMapper.findByMemberWithRoles("testuser")).thenReturn(memberWithRoles);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        loginRequest.setPassword("wrongpassword");

        // When & Then
        CustomBusinessException exception = assertThrows(CustomBusinessException.class, () -> {
            memberService.login(loginRequest);
        });

        assertEquals("잘못된 사용자 정보를 입력 하셨습니다.", exception.getMessage());

        verify(memberMapper, times(1)).findByMemberWithRoles("testuser");
        verify(passwordEncoder, times(1)).matches("wrongpassword", "encodedPassword");
        verify(jwtTokenProvider, never()).generateAccessToken(anyString(), anyList(), anyLong());
        verify(jwtTokenProvider, never()).generateRefreshToken(anyString(), anyLong());
        verify(jwtTokenRedisService, never()).saveRefreshToken(anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("로그인 성공 - 여러 역할을 가진 사용자")
    void login_Success_UserWithMultipleRoles() {
        // Given
        memberWithRoles.setLoginId("adminuser");
        memberWithRoles.setRoles("[\"ROLE_USER\",\"ROLE_ADMIN\"]"); // JSON 문자열 형태로 설정
        
        when(memberMapper.findByMemberWithRoles("adminuser")).thenReturn(memberWithRoles);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(anyString(), anyList(), anyLong())).thenReturn("admin-access-token");
        when(jwtTokenProvider.generateRefreshToken(anyString(), anyLong())).thenReturn("admin-refresh-token");

        loginRequest.setLoginId("adminuser");

        // When
        ResponseEntity<TokenResponse> response = memberService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("admin-access-token", response.getBody().getAccessToken());
        assertEquals("admin-refresh-token", response.getBody().getRefreshToken());

        verify(memberMapper, times(1)).findByMemberWithRoles("adminuser");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
        verify(jwtTokenProvider, times(1)).generateAccessToken("adminuser", Arrays.asList("ROLE_USER", "ROLE_ADMIN"), 3600000L);
        verify(jwtTokenProvider, times(1)).generateRefreshToken("adminuser", 604800000L);
        verify(jwtTokenRedisService, times(1)).saveRefreshToken("adminuser", "admin-refresh-token", 604800000L);
    }

    @Test
    @DisplayName("로그인 성공 - 역할이 없는 사용자")
    void login_Success_UserWithNoRoles() {
        // Given
        memberWithRoles.setLoginId("noroleuser");
        memberWithRoles.setRoles("[]"); // 빈 역할 배열
        
        when(memberMapper.findByMemberWithRoles("noroleuser")).thenReturn(memberWithRoles);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(anyString(), anyList(), anyLong())).thenReturn("no-role-access-token");
        when(jwtTokenProvider.generateRefreshToken(anyString(), anyLong())).thenReturn("no-role-refresh-token");

        loginRequest.setLoginId("noroleuser");

        // When
        ResponseEntity<TokenResponse> response = memberService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("no-role-access-token", response.getBody().getAccessToken());
        assertEquals("no-role-refresh-token", response.getBody().getRefreshToken());

        verify(memberMapper, times(1)).findByMemberWithRoles("noroleuser");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
        verify(jwtTokenProvider, times(1)).generateAccessToken("noroleuser", Arrays.asList(), 3600000L);
        verify(jwtTokenProvider, times(1)).generateRefreshToken("noroleuser", 604800000L);
        verify(jwtTokenRedisService, times(1)).saveRefreshToken("noroleuser", "no-role-refresh-token", 604800000L);
    }
}
