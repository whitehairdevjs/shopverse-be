package org.biz.shopverse.controller;

import org.biz.shopverse.dto.auth.TokenRequest;
import org.biz.shopverse.security.JwtTokenProvider;
import org.biz.shopverse.service.TestService;
import org.biz.shopverse.service.auth.JwtTokenRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;
    private final JwtTokenRedisService jwtTokenRedisService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("hello")
    public String hello() {
        return testService.getString();
    }

    @PostMapping("get-role")
    public String getUserRoleTest(@RequestBody TokenRequest tokenRequest) {
        System.out.println(tokenRequest.getRefreshToken());
        String result = jwtTokenProvider.getUserRole(tokenRequest.getRefreshToken());
        System.out.println(result);
        return result;
    }

}