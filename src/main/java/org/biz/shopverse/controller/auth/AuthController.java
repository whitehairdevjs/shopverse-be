package org.biz.shopverse.controller.auth;

import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.biz.shopverse.dto.auth.TokenRequest;
import org.biz.shopverse.dto.auth.TokenResponse;
import org.biz.shopverse.dto.member.request.MemberCreateRequest;
import org.biz.shopverse.security.JwtTokenProvider;
import org.biz.shopverse.service.auth.JwtTokenRedisService;
import org.biz.shopverse.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final MemberService memberService;
    private final JwtTokenRedisService jwtTokenRedisService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenValidityInMs;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidityInMs;

//    @PostMapping("/login")
//    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
//        Member member = memberService.findByLoginId("js");
//
//        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
//            throw new BadCredentialsException("잘못된 로그인 정보");
//        }
//
//        String accessToken = jwtTokenProvider.generateAccessToken(member.getLoginId(), member.getRole(), accessTokenValidityInMs);
//        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId(), refreshTokenValidityInMs);
//
//        jwtTokenRedisService.saveRefreshToken(user.getUserId(), refreshToken, refreshTokenValidityInMs);  // 7일
//
//        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
//    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid MemberCreateRequest request) {
        // 1. 중복 유저 아이디 체크
        if (memberService.existsByLoginId(request.getLoginId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 사용 중인 아이디입니다.");
        }

//        // 2. 비밀번호 암호화
//        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
//        userRequest.setPassword(encodedPassword);
//
//        // 3. 회원 저장
//        userService.createUser(userRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("회원가입이 완료되었습니다.");
    }

//    @PostMapping("/reissue")
//    public ResponseEntity<TokenResponse> reissue(@RequestBody TokenRequest request) {
//        String refreshToken = request.getRefreshToken();
//
//        if (!jwtTokenProvider.isTokenValid(refreshToken)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        String userId = jwtTokenProvider.getUserId(refreshToken);
//        String savedToken = jwtTokenRedisService.getRefreshToken(userId);
//
//        if (!refreshToken.equals(savedToken)) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        List<String> roleList = userService.findRolesByUserId(userId);
//
//        if (roleList.size() <= 0) {
//            throw new IllegalArgumentException("Access Token 갱신중에 사용자의 권한이 존재하지 않습니다.");
//        }
//
//        String accessToken = jwtTokenProvider.generateAccessToken(userId, roleList.get(0), accessTokenValidityInMs);
//
//        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
//    }

}