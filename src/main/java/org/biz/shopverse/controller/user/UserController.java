package org.biz.shopverse.controller.user;

import org.biz.shopverse.dto.user.UserRequest;
import org.biz.shopverse.dto.user.UserResponse;
import org.biz.shopverse.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(
        summary = "유저 정보 조회",
        description = "userId에 해당하는 유저의 상세 정보를 반환합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "정상 조회"),
            @ApiResponse(responseCode = "404", description = "유저 없음")
        }
    )
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/info")
    public UserResponse selectUserById(@RequestBody UserRequest user) {
        return userService.selectUserById(user);
    }

}
