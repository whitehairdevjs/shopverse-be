package org.biz.shopverse.controller.auth;

import org.biz.shopverse.dto.auth.LoginRequest;
import org.biz.shopverse.dto.auth.TokenRequest;
import org.biz.shopverse.dto.auth.TokenResponse;
import org.biz.shopverse.dto.user.UserResponse;
import org.biz.shopverse.security.JwtTokenProvider;
import org.biz.shopverse.service.auth.JwtTokenRedisService;
import org.biz.shopverse.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 1) POST /auth/login → 아이디/비밀번호 검증
 * 2) 성공 시 JwtTokenProvider.createToken() 호출 → 토큰 발급
 * 3) 클라이언트에 토큰 전달
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final JwtTokenRedisService jwtTokenRedisService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        UserResponse user = userService.findByUserId(loginRequest.getUserId());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("잘못된 로그인 정보");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getRoleCode());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());

        jwtTokenRedisService.saveRefreshToken(user.getUserId(), refreshToken, 604800000);  // 7일

        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@RequestBody TokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.isTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = jwtTokenProvider.getUserId(refreshToken);
        String savedToken = jwtTokenRedisService.getRefreshToken(userId);

        if (!refreshToken.equals(savedToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<String> roleList = userService.findRolesByUserId(userId);

        if (roleList.size() <= 0) {
            throw new IllegalArgumentException("Access Token 갱신중에 사용자의 권한이 존재하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(userId, roleList.get(0));

        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
    }

}