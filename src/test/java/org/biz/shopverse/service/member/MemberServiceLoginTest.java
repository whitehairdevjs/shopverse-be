package org.biz.shopverse.service.member;

import jakarta.servlet.http.HttpServletResponse;
import org.biz.shopverse.dto.auth.TokenResponse;
import org.biz.shopverse.dto.common.ApiResponse;
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
import org.springframework.http.HttpStatus;
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

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private MemberService memberService;

    private MemberLoginRequest loginRequest;
    private MemberWithRoles memberWithRoles;

    @BeforeEach
    void setUp() {
        // JWT 토큰 만료 시간 설정
        ReflectionTestUtils.setField(memberService, "accessTokenValidityInMs", 3600000L); // 1시간
        ReflectionTestUtils.setField(memberService, "refreshTokenValidityInMs", 604800000L); // 7일
        ReflectionTestUtils.setField(memberService, "cookieSecure", false); // 개발 환경

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
        ResponseEntity<ApiResponse<String>> responseEntity = memberService.login(loginRequest, response);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().isSuccess());
        assertEquals("로그인이 완료되었습니다.", responseEntity.getBody().getMessage());

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
            memberService.login(loginRequest, response);
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
            memberService.login(loginRequest, response);
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
        memberWithRoles.setRoles("[\"ROLE_USER\",\"ROLE_ADMIN\"]"); // JSON 문자열 형태로 설정
        
        when(memberMapper.findByMemberWithRoles("adminuser")).thenReturn(memberWithRoles);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(anyString(), anyList(), anyLong())).thenReturn("admin-access-token");
        when(jwtTokenProvider.generateRefreshToken(anyString(), anyLong())).thenReturn("admin-refresh-token");

        loginRequest.setLoginId("adminuser");

        // When
        ResponseEntity<ApiResponse<String>> responseEntity = memberService.login(loginRequest, response);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().isSuccess());
        assertEquals("로그인이 완료되었습니다.", responseEntity.getBody().getMessage());

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
        memberWithRoles.setRoles("[]"); // 빈 역할 배열
        
        when(memberMapper.findByMemberWithRoles("noroleuser")).thenReturn(memberWithRoles);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(anyString(), anyList(), anyLong())).thenReturn("no-role-access-token");
        when(jwtTokenProvider.generateRefreshToken(anyString(), anyLong())).thenReturn("no-role-refresh-token");

        loginRequest.setLoginId("noroleuser");

        // When
        ResponseEntity<ApiResponse<String>> responseEntity = memberService.login(loginRequest, response);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().isSuccess());
        assertEquals("로그인이 완료되었습니다.", responseEntity.getBody().getMessage());

        verify(memberMapper, times(1)).findByMemberWithRoles("noroleuser");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
        verify(jwtTokenProvider, times(1)).generateAccessToken("noroleuser", Arrays.asList(), 3600000L);
        verify(jwtTokenProvider, times(1)).generateRefreshToken("noroleuser", 604800000L);
        verify(jwtTokenRedisService, times(1)).saveRefreshToken("noroleuser", "no-role-refresh-token", 604800000L);
    }

    // ==================== 로그아웃 테스트 ====================
    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logout_Success() {
        // When
        ResponseEntity<ApiResponse<String>> responseEntity = memberService.logout(response);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().isSuccess());
        assertEquals("로그아웃이 완료되었습니다.", responseEntity.getBody().getMessage());
    }

    // ==================== 토큰 갱신 테스트 ====================
    @Test
    @DisplayName("토큰 갱신 성공 - 유효한 refreshToken")
    void reIssueAccessToken_Success() {
        // Given
        String validRefreshToken = "valid-refresh-token";
        String loginId = "testuser";
        String newAccessToken = "new-access-token";
        
        when(jwtTokenProvider.isTokenValid(validRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserId(validRefreshToken)).thenReturn(loginId);
        when(jwtTokenRedisService.getRefreshToken(loginId)).thenReturn(validRefreshToken);
        when(memberMapper.findByMemberWithRoles(loginId)).thenReturn(memberWithRoles);
        when(jwtTokenProvider.generateAccessToken(anyString(), any(), anyLong())).thenReturn(newAccessToken);

        // When
        ResponseEntity<ApiResponse<String>> responseEntity = memberService.reissueAccessToken(null, response);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().isSuccess());
        assertEquals("액세스 토큰이 갱신되었습니다.", responseEntity.getBody().getMessage());

        verify(jwtTokenProvider, times(1)).isTokenValid(validRefreshToken);
        verify(jwtTokenProvider, times(1)).getUserId(validRefreshToken);
        verify(jwtTokenRedisService, times(1)).getRefreshToken(loginId);
        verify(memberMapper, times(1)).findByMemberWithRoles(loginId);
        verify(jwtTokenProvider, times(1)).generateAccessToken(loginId, Arrays.asList("ROLE_USER"), 3600000L);
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 유효하지 않은 refreshToken")
    void reIssueAccessToken_Failure_InvalidToken() {
        // Given
        String invalidRefreshToken = "invalid-refresh-token";
        
        when(jwtTokenProvider.isTokenValid(invalidRefreshToken)).thenReturn(false);

        // When & Then
        CustomBusinessException exception = assertThrows(CustomBusinessException.class, () -> {
            memberService.reissueAccessToken(null, response);
        });

        assertEquals("유효하지 않은 리프레시 토큰입니다.", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());

        verify(jwtTokenProvider, times(1)).isTokenValid(invalidRefreshToken);
        verify(jwtTokenProvider, never()).getUserId(anyString());
        verify(jwtTokenRedisService, never()).getRefreshToken(anyString());
        verify(memberMapper, never()).findByMemberWithRoles(anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(anyString(), any(), anyLong());
    }

    @Test
    @DisplayName("토큰 갱신 실패 - Redis에 저장된 토큰과 일치하지 않는 경우")
    void reIssueAccessToken_Failure_TokenMismatch() {
        // Given
        String validRefreshToken = "valid-refresh-token";
        String loginId = "testuser";
        String storedRefreshToken = "different-refresh-token";
        
        when(jwtTokenProvider.isTokenValid(validRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserId(validRefreshToken)).thenReturn(loginId);
        when(jwtTokenRedisService.getRefreshToken(loginId)).thenReturn(storedRefreshToken);

        // When & Then
        CustomBusinessException exception = assertThrows(CustomBusinessException.class, () -> {
            memberService.reissueAccessToken(null, response);
        });

        assertEquals("저장된 리프레시 토큰과 일치하지 않습니다.", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());

        verify(jwtTokenProvider, times(1)).isTokenValid(validRefreshToken);
        verify(jwtTokenProvider, times(1)).getUserId(validRefreshToken);
        verify(jwtTokenRedisService, times(1)).getRefreshToken(loginId);
        verify(memberMapper, never()).findByMemberWithRoles(anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(anyString(), any(), anyLong());
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 사용자를 찾을 수 없는 경우")
    void reIssueAccessToken_Failure_UserNotFound() {
        // Given
        String validRefreshToken = "valid-refresh-token";
        String loginId = "nonexistent";
        
        when(jwtTokenProvider.isTokenValid(validRefreshToken)).thenReturn(true);
        when(jwtTokenProvider.getUserId(validRefreshToken)).thenReturn(loginId);
        when(jwtTokenRedisService.getRefreshToken(loginId)).thenReturn(validRefreshToken);
        when(memberMapper.findByMemberWithRoles(loginId)).thenReturn(null);

        // When & Then
        CustomBusinessException exception = assertThrows(CustomBusinessException.class, () -> {
            memberService.reissueAccessToken(null, response);
        });

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());

        verify(jwtTokenProvider, times(1)).isTokenValid(validRefreshToken);
        verify(jwtTokenProvider, times(1)).getUserId(validRefreshToken);
        verify(jwtTokenRedisService, times(1)).getRefreshToken(loginId);
        verify(memberMapper, times(1)).findByMemberWithRoles(loginId);
        verify(jwtTokenProvider, never()).generateAccessToken(anyString(), any(), anyLong());
    }
}
