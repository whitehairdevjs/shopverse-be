package org.biz.shopverse.dto.member;

import lombok.Data;

import java.util.List;

@Data
public class MemberWithRoles {
    private String name;
    private String loginId;
    private String password;
    private List<String> roles;
}
