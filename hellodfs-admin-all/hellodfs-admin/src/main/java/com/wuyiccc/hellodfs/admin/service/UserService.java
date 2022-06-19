package com.wuyiccc.hellodfs.admin.service;

import com.wuyiccc.hellodfs.admin.common.pojo.User;
import com.wuyiccc.hellodfs.admin.common.pojo.dto.UserCreateDTO;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author wuyiccc
 * @date 2022/6/4 15:15
 */
public interface UserService {


    public User createUser(UserCreateDTO userCreateDTO);

    public User findUserByUsername(String username);
}
