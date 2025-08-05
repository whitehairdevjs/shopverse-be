package org.biz.shopverse.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.biz.shopverse.dto.common.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 인증/인가 실패 시 401 응답
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<String> errorResponse = ApiResponse.error("Unauthorized", "인증이 필요합니다.", 401);
        errorResponse.setDetails(Map.of("path", request.getRequestURI()));

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}