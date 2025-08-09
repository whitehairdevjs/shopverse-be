package org.biz.shopverse.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.biz.shopverse.dto.common.ApiResponse;
import org.biz.shopverse.dto.member.request.MemberLoginRequest;
import org.biz.shopverse.exception.CustomBusinessException;
import org.biz.shopverse.exception.GlobalExceptionHandler;
import org.biz.shopverse.service.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // @Value 어노테이션으로 주입되는 값들을 테스트에서 설정
        ReflectionTestUtils.setField(memberController, "accessTokenValidityInMs", 900000L); // 15분
        ReflectionTestUtils.setField(memberController, "refreshTokenValidityInMs", 604800000L); // 7일

        // GlobalExceptionHandler를 포함하여 MockMvc 설정
        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    // ==================== 로그인 성공 테스트 ====================
    @Test
    @DisplayName("로그인 성공 - 정상적인 사용자 정보")
    void login_Success() throws Exception {
        // Given
        MemberLoginRequest loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId("testuser");
        loginRequest.setPassword("password123");

        ResponseEntity<ApiResponse<String>> successResponse = ResponseEntity.ok(
                ApiResponse.success("로그인이 완료되었습니다.")
        );

        when(memberService.login(any(MemberLoginRequest.class), any())).thenReturn(successResponse);

        // When & Then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인이 완료되었습니다."));
    }

    // ==================== 로그인 실패 테스트 ====================
    @Test
    @DisplayName("로그인 실패 - 잘못된 사용자 정보")
    void login_Failure_InvalidCredentials() throws Exception {
        // Given
        MemberLoginRequest loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId("wronguser");
        loginRequest.setPassword("wrongpassword");

        when(memberService.login(any(MemberLoginRequest.class), any()))
                .thenThrow(new CustomBusinessException("잘못된 사용자 정보를 입력 하셨습니다.", HttpStatus.BAD_REQUEST));

        // When & Then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("잘못된 사용자 정보를 입력 하셨습니다."));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_Failure_UserNotFound() throws Exception {
        // Given
        MemberLoginRequest loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId("nonexistent");
        loginRequest.setPassword("password123");

        when(memberService.login(any(MemberLoginRequest.class), any()))
                .thenThrow(new CustomBusinessException("권한, 정보가 존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND));

        // When & Then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("권한, 정보가 존재하지 않는 사용자입니다."));
    }

    // ==================== 리프레시 토큰 테스트 ====================
    @Test
    @DisplayName("리프레시 토큰 성공 - 유효한 refreshToken")
    void refreshToken_Success() throws Exception {
        // Given
        ResponseEntity<ApiResponse<String>> successResponse = ResponseEntity.ok(
                ApiResponse.success("액세스 토큰이 갱신되었습니다.")
        );

        when(memberService.reissueAccessToken(any(), any())).thenReturn(successResponse);

        // When & Then
        mockMvc.perform(post("/member/refresh-token")
                        .cookie(new jakarta.servlet.http.Cookie("refreshToken", "valid-refresh-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("액세스 토큰이 갱신되었습니다."));
    }

    @Test
    @DisplayName("리프레시 토큰 실패 - refreshToken이 없는 경우")
    void refreshToken_Failure_NoToken() throws Exception {
        // Given
        ResponseEntity<ApiResponse<String>> errorResponse = ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("인증 실패", "리프레시 토큰이 제공되지 않았습니다.", 401));

        when(memberService.reissueAccessToken(any(), any())).thenReturn(errorResponse);

        // When & Then
        mockMvc.perform(post("/member/refresh-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("리프레시 토큰이 제공되지 않았습니다."));
    }

    @Test
    @DisplayName("리프레시 토큰 실패 - 유효하지 않은 refreshToken")
    void refreshToken_Failure_InvalidToken() throws Exception {
        // Given
        when(memberService.reissueAccessToken(any(), any()))
                .thenThrow(new CustomBusinessException("유효하지 않은 리프레시 토큰입니다.", HttpStatus.UNAUTHORIZED));

        // When & Then
        mockMvc.perform(post("/member/refresh-token")
                        .cookie(new jakarta.servlet.http.Cookie("refreshToken", "invalid-refresh-token")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("유효하지 않은 리프레시 토큰입니다."));
    }

    @Test
    @DisplayName("리프레시 토큰 실패 - Redis에 저장된 토큰과 일치하지 않는 경우")
    void refreshToken_Failure_TokenMismatch() throws Exception {
        // Given
        when(memberService.reissueAccessToken(any(), any()))
                .thenThrow(new CustomBusinessException("저장된 리프레시 토큰과 일치하지 않습니다.", HttpStatus.UNAUTHORIZED));

        // When & Then
        mockMvc.perform(post("/member/refresh-token")
                        .cookie(new jakarta.servlet.http.Cookie("refreshToken", "valid-refresh-token")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("저장된 리프레시 토큰과 일치하지 않습니다."));
    }

    @Test
    @DisplayName("리프레시 토큰 실패 - 사용자를 찾을 수 없는 경우")
    void refreshToken_Failure_UserNotFound() throws Exception {
        // Given
        when(memberService.reissueAccessToken(any(), any()))
                .thenThrow(new CustomBusinessException("사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED));

        // When & Then
        mockMvc.perform(post("/member/refresh-token")
                        .cookie(new jakarta.servlet.http.Cookie("refreshToken", "valid-refresh-token")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("리프레시 토큰 실패 - 토큰 처리 중 예외 발생")
    void refreshToken_Failure_TokenException() throws Exception {
        // Given
        when(memberService.reissueAccessToken(any(), any()))
                .thenThrow(new RuntimeException("토큰 처리 오류"));

        // When & Then
        mockMvc.perform(post("/member/refresh-token")
                        .cookie(new jakarta.servlet.http.Cookie("refreshToken", "valid-refresh-token")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("토큰 처리 중 오류가 발생했습니다."));
    }

    // ==================== 로그아웃 테스트 ====================
    @Test
    @DisplayName("로그아웃 성공")
    void logout_Success() throws Exception {
        // Given
        ResponseEntity<ApiResponse<String>> successResponse = ResponseEntity.ok(
                ApiResponse.success("로그아웃이 완료되었습니다.")
        );

        when(memberService.logout(any())).thenReturn(successResponse);

        // When & Then
        mockMvc.perform(post("/member/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그아웃이 완료되었습니다."));
    }

    // ==================== 입력 유효성 검증 테스트 ====================
    @Test
    @DisplayName("로그인 요청 - 빈 로그인 ID")
    void login_EmptyLoginId() throws Exception {
        // Given
        MemberLoginRequest loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId("");
        loginRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 요청 - 빈 비밀번호")
    void login_EmptyPassword() throws Exception {
        // Given
        MemberLoginRequest loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId("testuser");
        loginRequest.setPassword("");

        // When & Then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 요청 - null 로그인 ID")
    void login_NullLoginId() throws Exception {
        // Given
        MemberLoginRequest loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId(null);
        loginRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 요청 - null 비밀번호")
    void login_NullPassword() throws Exception {
        // Given
        MemberLoginRequest loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId("testuser");
        loginRequest.setPassword(null);

        // When & Then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    // ==================== HTTP 프로토콜 검증 테스트 ====================
    @Test
    @DisplayName("로그인 요청 - 잘못된 JSON 형식")
    void login_InvalidJsonFormat() throws Exception {
        // Given
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 요청 - Content-Type이 없는 경우")
    void login_NoContentType() throws Exception {
        // Given
        MemberLoginRequest loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId("testuser");
        loginRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/member/login")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("로그인 요청 - 빈 요청 본문")
    void login_EmptyRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 요청 - 잘못된 Content-Type")
    void login_WrongContentType() throws Exception {
        // Given
        MemberLoginRequest loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId("testuser");
        loginRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnsupportedMediaType());
    }

    // ==================== 엔드포인트 경로 테스트 ====================
    @Test
    @DisplayName("로그인 요청 - 잘못된 URL 경로")
    void login_WrongUrlPath() throws Exception {
        // Given
        MemberLoginRequest loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId("testuser");
        loginRequest.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/wrong/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound());
    }

    // ==================== 서비스 예외 처리 테스트 ====================
    @Test
    @DisplayName("로그인 요청 - 서비스에서 예외 발생")
    void login_ServiceException() throws Exception {
        // Given
        MemberLoginRequest loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId("testuser");
        loginRequest.setPassword("password123");

        when(memberService.login(any(MemberLoginRequest.class), any()))
                .thenThrow(new RuntimeException("서비스 내부 오류"));

        // When & Then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("서비스 내부 오류"));
    }
}
