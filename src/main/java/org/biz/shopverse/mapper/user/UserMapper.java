package org.biz.shopverse.mapper.user;

import org.biz.shopverse.dto.user.UserRequest;
import org.biz.shopverse.dto.user.UserResponse;

import java.util.List;

public interface UserMapper {
    UserResponse selectUserById(UserRequest user);

    UserResponse findByUserId(String userId);

    List<String> findRolesByUserId(String userId);
}