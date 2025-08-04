package org.biz.shopverse.domain.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    private Long id;
    private String loginId;
    private String password;
    private String name;
    private String nickname;
    private String phone;
    private String email;
    private String gender;
    private LocalDate birthDate;
    private Boolean isSocial;
    private String socialProvider;
    private Boolean marketingYn;
    private Boolean smsYn;
    private Boolean emailYn;
    private Integer point;
    private String status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 