package org.biz.shopverse.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.biz.shopverse.dto.common.ApiResponse;
import org.biz.shopverse.exception.auth.JwtInvalidException;
import org.biz.shopverse.exception.auth.JwtTokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.biz.shopverse.service.auth.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);

            if (token != null && jwtTokenProvider.isTokenValid(token)) {
                String userId = jwtTokenProvider.getUserId(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);
        } catch (JwtTokenExpiredException e) {
            writeJwtErrorResponse(response, "JWT Token Expired", "토큰이 만료되었습니다.", request.getRequestURI());
        } catch (JwtInvalidException e) {
            writeJwtErrorResponse(response, "Invalid JWT Token", "유효하지 않은 토큰입니다.", request.getRequestURI());
        }
    }

    private void writeJwtErrorResponse(HttpServletResponse response, String error, String message, String path) throws IOException {
        Map<String, Object> details = new HashMap<>();
        details.put("path", path);
        details.put("exception", "JwtAuthenticationException");
        details.put("tokenType", "JWT");
        
        ApiResponse<String> errorResponse = ApiResponse.error(error, message, 401);
        errorResponse.setDetails(details);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        
        return null;
    }

}