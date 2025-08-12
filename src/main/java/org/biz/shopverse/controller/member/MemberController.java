package org.biz.shopverse.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.biz.shopverse.dto.auth.TokenResponse;
import org.biz.shopverse.dto.common.ApiResponse;
import org.biz.shopverse.dto.member.request.MemberCreateRequest;
import org.biz.shopverse.dto.member.request.MemberLoginRequest;
import org.biz.shopverse.dto.member.response.MemberResponse;
import org.biz.shopverse.service.member.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody @Valid MemberLoginRequest memberLoginRequest, HttpServletResponse response) {
        return memberService.login(memberLoginRequest, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        return memberService.logout(response);
    }

    @PostMapping("/reissue-access-token")
    public ResponseEntity<ApiResponse<TokenResponse>> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        return memberService.reissueAccessToken(request, response);
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody @Valid MemberCreateRequest memberCreateRequest) {
        if (memberService.signup(memberCreateRequest)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("회원가입이 완료되었습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("회원가입 실패", "회원가입에 실패했습니다.", 500));
        }
    }

    @GetMapping("/check-login-id")
    public ResponseEntity<ApiResponse<String>> checkLoginIdDuplication(@RequestParam String loginId) {
        return ResponseEntity.ok(ApiResponse.success(String.valueOf(memberService.existsByLoginId(loginId))));
    }

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<String>> checkEmailDuplication(@RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.success(String.valueOf(memberService.existsByEmail(email))));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<MemberResponse>> getProfile(HttpServletRequest request) {
        return memberService.getProfile(request);
    }

}