package org.biz.shopverse.dto.member;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MemberWithRoles {
    private String name;
    private String loginId;
    private String password;
    private String roles;

    public List<String> getRolesList() {
        if (roles == null || "null".equals(roles) || roles.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(roles, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}