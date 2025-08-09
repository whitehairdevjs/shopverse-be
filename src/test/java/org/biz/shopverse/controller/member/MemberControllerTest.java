package org.biz.shopverse.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.biz.shopverse.dto.auth.TokenResponse;
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

        TokenResponse tokenResponse = new TokenResponse("access-token", "refresh-token");
        ResponseEntity<TokenResponse> responseEntity = ResponseEntity.ok(tokenResponse);

        when(memberService.login(any(MemberLoginRequest.class))).thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    // ==================== 로그인 실패 테스트 ====================
    @Test
    @DisplayName("로그인 실패 - 잘못된 사용자 정보")
    void login_Failure_InvalidCredentials() throws Exception {
        // Given
        MemberLoginRequest loginRequest = new MemberLoginRequest();
        loginRequest.setLoginId("wronguser");
        loginRequest.setPassword("wrongpassword");

        when(memberService.login(any(MemberLoginRequest.class)))
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

        when(memberService.login(any(MemberLoginRequest.class)))
                .thenThrow(new CustomBusinessException("권한, 정보가 존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND));

        // When & Then
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("권한, 정보가 존재하지 않는 사용자입니다."));
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

        when(memberService.login(any(MemberLoginRequest.class)))
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
