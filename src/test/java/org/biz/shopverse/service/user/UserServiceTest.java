package org.biz.shopverse.service.user;

import org.biz.shopverse.dto.user.UserRequest;
import org.biz.shopverse.dto.user.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void testSelectUserById() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUserId("user001");
        UserResponse userResponse = userService.selectUserById(userRequest);

        System.out.println(userResponse);
    }

}
