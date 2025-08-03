package org.biz.shopverse.service.user;

import org.biz.shopverse.dto.user.UserRequest;
import org.biz.shopverse.dto.user.UserResponse;
import org.biz.shopverse.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    public UserResponse selectUserById(UserRequest user) {
        return userMapper.selectUserById(user);
    }

    public UserResponse findByUserId(String userId) {
        return userMapper.findByUserId(userId);
    }

    public List<String> findRolesByUserId(String userId) {
        return userMapper.findRolesByUserId(userId);
    }
}
