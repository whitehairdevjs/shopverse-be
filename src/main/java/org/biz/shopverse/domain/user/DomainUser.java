package org.biz.shopverse.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomainUser {
    private String userId;
    private String userName;
    private String password;
    private String userUuid;
    private String mobileNumber;
    private String birth;
    private String zipcode;
    private String address;
    private String addressDetail;
    private String accessIp;
    private boolean useYn;
    private String registerDate; // 또는 LocalDate
    private String modifyDate;   // 또는 LocalDate
}