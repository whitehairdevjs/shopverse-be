package org.biz.shopverse.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String userId;
    private String userName;
    private String password;
    private String mobileNumber;
    private String birth;
    private String zipcode;
    private String address;
    private String addressDetail;
    private String roleId;
    private String roleCode;
}
