package org.biz.shopverse.dto.auth;

import lombok.Data;

import java.util.List;

@Data
public class MemberWithRoles {
    private String loginId;
    private String password;
    private List<String> roles;
}
