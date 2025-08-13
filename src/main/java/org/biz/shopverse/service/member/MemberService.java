package org.biz.shopverse.service.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.biz.shopverse.domain.member.Member;
import org.biz.shopverse.dto.auth.TokenResponse;
import org.biz.shopverse.dto.common.ApiResponse;
import org.biz.shopverse.dto.member.MemberWithRoles;
import org.biz.shopverse.dto.member.request.MemberCreateRequest;
import org.biz.shopverse.dto.member.request.MemberLoginRequest;
import org.biz.shopverse.dto.member.request.MemberUpdateRequest;
import org.biz.shopverse.dto.member.response.MemberResponse;
import org.biz.shopverse.exception.CustomBusinessException;
import org.biz.shopverse.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.biz.shopverse.security.JwtTokenProvider;
import org.biz.shopverse.service.auth.JwtTokenRedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenRedisService jwtTokenRedisService;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenValidityInMs;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidityInMs;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    public MemberResponse findByLoginId(String loginId) {
        return memberMapper.findByLoginId(loginId);
    }

    public boolean existsByLoginId(String loginId) {
        return memberMapper.existsByLoginId(loginId);
    }

    public boolean existsByEmail(String email) {
        return memberMapper.existsByEmail(email);
    }

    public MemberWithRoles findByMemberWithRoles(String loginId) {
        return memberMapper.findByMemberWithRoles(loginId);
    }

    public int createMember(MemberCreateRequest memberCreateRequest) {
        try {
            return memberMapper.createMember(memberCreateRequest);
        } catch (Exception e) {
            throw new CustomBusinessException("회원가입 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ApiResponse<TokenResponse>> login(MemberLoginRequest memberLoginRequest, HttpServletResponse response) {
        MemberWithRoles memberWithRoles = findByMemberWithRoles(memberLoginRequest.getLoginId());

        if (memberWithRoles == null) {
            throw new CustomBusinessException("권한, 정보가 존재하지 않는 사용자입니다.");
        }

        if (!passwordEncoder.matches(memberLoginRequest.getPassword(), memberWithRoles.getPassword())) {
            throw new CustomBusinessException("잘못된 사용자 정보를 입력 하셨습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(memberWithRoles.getLoginId(), memberWithRoles.getRolesList(), accessTokenValidityInMs);
        String refreshToken = jwtTokenProvider.generateRefreshToken(memberWithRoles.getLoginId(), refreshTokenValidityInMs);

        jwtTokenRedisService.saveRefreshToken(memberWithRoles.getLoginId(), refreshToken, refreshTokenValidityInMs);  // 7일

        setHttpOnlyCookie(response, "refreshToken", refreshToken, refreshTokenValidityInMs / 1000);

        Member member = Member.builder()
                .loginId(memberWithRoles.getLoginId())
                .name(memberWithRoles.getName())
                .build();

        TokenResponse tokenResponse = TokenResponse.builder()
                .member(member)
                .accessToken(accessToken)
                .build();

        return ResponseEntity.ok(ApiResponse.success(tokenResponse, "로그인이 완료되었습니다."));
    }

    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        deleteCookie(response, "refreshToken");

        return ResponseEntity.ok(ApiResponse.success("로그아웃이 완료되었습니다."));
    }

    public ResponseEntity<ApiResponse<TokenResponse>> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = null;
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("refreshToken".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                    }
                }
            }

            if (refreshToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("인증 실패", "리프레시 토큰이 제공되지 않았습니다.", 401));
            }

            if (!jwtTokenProvider.isTokenValid(refreshToken)) {
                throw new CustomBusinessException("유효하지 않은 리프레시 토큰입니다.", HttpStatus.UNAUTHORIZED);
            }

            String loginId = jwtTokenProvider.getUserId(refreshToken);

            String storedRefreshToken = jwtTokenRedisService.getRefreshToken(loginId);
            if (storedRefreshToken == null || !refreshToken.equals(storedRefreshToken)) {
                throw new CustomBusinessException("저장된 리프레시 토큰과 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
            }

            MemberWithRoles memberWithRoles = findByMemberWithRoles(loginId);
            if (memberWithRoles == null) {
                throw new CustomBusinessException("사용자를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED);
            }

            String newAccessToken = jwtTokenProvider.generateAccessToken(
                    memberWithRoles.getLoginId(),
                    memberWithRoles.getRolesList(),
                    accessTokenValidityInMs
            );

            TokenResponse tokenResponse = TokenResponse.builder()
                    .accessToken(newAccessToken)
                    .build();

            return ResponseEntity.ok(ApiResponse.success(tokenResponse, "액세스 토큰이 갱신되었습니다."));
        } catch (CustomBusinessException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(ApiResponse.error("인증 실패", e.getMessage(), e.getHttpStatus().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증 실패", "토큰 처리 중 오류가 발생했습니다.", 401));
        }
    }

    public boolean signup(MemberCreateRequest memberCreateRequest) {
        if (existsByLoginId(memberCreateRequest.getLoginId())) {
            throw new CustomBusinessException("이미 사용중인 ID 입니다.", HttpStatus.CONFLICT);
        }

        if (existsByEmail(memberCreateRequest.getEmail())) {
            throw new CustomBusinessException("이미 사용중인 이메일 입니다.", HttpStatus.CONFLICT);
        }

        String encodedPassword = passwordEncoder.encode(memberCreateRequest.getPassword());
        memberCreateRequest.setPassword(encodedPassword);

        return createMember(memberCreateRequest) > 0;
    }

    public ResponseEntity<ApiResponse<MemberResponse>> getProfile(HttpServletRequest request) {
        try {
            String accessToken = null;
            
            String bearer = request.getHeader("Authorization");
            if (bearer != null && bearer.startsWith("Bearer ")) {
                accessToken = bearer.substring(7);
            }
        
            if (accessToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증 실패", "액세스 토큰이 제공되지 않았습니다.", 401));
            }

            if (!jwtTokenProvider.isTokenValid(accessToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증 실패", "유효하지 않은 토큰입니다.", 401));
            }

            String loginId = jwtTokenProvider.getUserId(accessToken);
            String role = jwtTokenProvider.getUserRole(accessToken);
            
            MemberResponse memberResponse = findByLoginId(loginId);
            if (memberResponse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("사용자 없음", "사용자를 찾을 수 없습니다.", 404));
            }

            memberResponse.setRole(role);

            return ResponseEntity.ok(ApiResponse.success(memberResponse, "프로필 조회 성공"));
        } catch (CustomBusinessException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(ApiResponse.error("프로필 조회 실패", e.getMessage(), e.getHttpStatus().value()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("서버 오류", "프로필 조회 중 오류가 발생했습니다.", 500));
        }
    }
    
    public ResponseEntity<ApiResponse<MemberResponse>> updateProfile(HttpServletRequest request, MemberUpdateRequest memberUpdateRequest) {
        try {
            String accessToken = null;
            
            String bearer = request.getHeader("Authorization");
            if (bearer != null && bearer.startsWith("Bearer ")) {
                accessToken = bearer.substring(7);
            }
        
            if (accessToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증 실패", "액세스 토큰이 제공되지 않았습니다.", 401));
            }

            if (!jwtTokenProvider.isTokenValid(accessToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증 실패", "유효하지 않은 토큰입니다.", 401));
            }

            String loginId = jwtTokenProvider.getUserId(accessToken);
            String role = jwtTokenProvider.getUserRole(accessToken);
            
            MemberResponse currentMember = findByLoginId(loginId);
            if (currentMember == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("사용자 없음", "사용자를 찾을 수 없습니다.", 404));
            }
            
            if (!currentMember.getEmail().equals(memberUpdateRequest.getEmail()) && 
                existsByEmail(memberUpdateRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error("이메일 중복", "이미 사용중인 이메일입니다.", 409));
            }
            
            int updateResult = memberMapper.updateMember(loginId, memberUpdateRequest);
            if (updateResult <= 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("수정 실패", "개인정보 수정에 실패했습니다.", 500));
            }
            
            currentMember.setName(memberUpdateRequest.getName());
            currentMember.setNickname(memberUpdateRequest.getNickname());
            currentMember.setPhone(memberUpdateRequest.getPhone());
            currentMember.setEmail(memberUpdateRequest.getEmail());
            currentMember.setGender(memberUpdateRequest.getGender());
            currentMember.setBirthDate(memberUpdateRequest.getBirthDate());
            currentMember.setMarketingYn(memberUpdateRequest.getMarketingYn());
            currentMember.setSmsYn(memberUpdateRequest.getSmsYn());
            currentMember.setEmailYn(memberUpdateRequest.getEmailYn());
            currentMember.setRole(role);
            
            return ResponseEntity.ok(ApiResponse.success(currentMember, "개인정보가 성공적으로 수정되었습니다."));
        } catch (CustomBusinessException e) {
            return ResponseEntity.status(e.getHttpStatus())
                    .body(ApiResponse.error("개인정보 수정 실패", e.getMessage(), e.getHttpStatus().value()));
        } catch (Exception e) {
            log.error("개인정보 수정 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("서버 오류", "개인정보 수정 중 오류가 발생했습니다.", 500));
        }
    }

    private void setHttpOnlyCookie(HttpServletResponse response, String name, String value, long maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure); // 설정 파일에서 읽어온 값 사용
        cookie.setPath("/");
        cookie.setMaxAge((int) maxAge);
        response.addCookie(cookie);
    }

    private void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure); // 설정 파일에서 읽어온 값 사용
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
} 