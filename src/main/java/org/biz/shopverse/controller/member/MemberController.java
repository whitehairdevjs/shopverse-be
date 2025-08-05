package org.biz.shopverse.controller.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.biz.shopverse.dto.member.request.MemberCreateRequest;
import org.biz.shopverse.service.member.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *  회원 가입, 조회, 수정, 탈퇴
 *
 */
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid MemberCreateRequest memberCreateRequest) {
        if (memberService.signup(memberCreateRequest)) {
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입에 실패했습니다.");
        }
    }

}