package com.wuyiccc.hellodfs.admin.service.impl;

import com.wuyiccc.hellodfs.admin.common.enumeration.ResponseStatusEnum;
import com.wuyiccc.hellodfs.admin.common.enumeration.SexEnum;
import com.wuyiccc.hellodfs.admin.common.exception.ThrowException;
import com.wuyiccc.hellodfs.admin.common.pojo.User;
import com.wuyiccc.hellodfs.admin.common.pojo.dto.UserCreateDTO;
import com.wuyiccc.hellodfs.admin.mapper.UserMapper;
import com.wuyiccc.hellodfs.admin.service.UserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author wuyiccc
 * @date 2022/6/4 15:15
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private Sid sid;

    @Autowired
    private UserMapper userMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public User createUser(UserCreateDTO userCreateDTO) {
        // 1. 检查用户名是否存在
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", userCreateDTO.getUsername());

        List<User> userList = userMapper.selectByExample(example);
        if (userList.size() >= 1) {
            ThrowException.display(ResponseStatusEnum.USER_EXISTS);
        }
        // 2. 创建用户
        String userId = sid.nextShort();
        User user = new User();
        user.setId(userId);
        user.setUsername(userCreateDTO.getUsername());
        String bPwd = BCrypt.hashpw(userCreateDTO.getPassword(), BCrypt.gensalt());
        user.setPassword(bPwd);
        user.setSex(SexEnum.SECRET.getValue());
        user.setTotalSize(0L);
        user.setUsedSize(0L);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());
        userMapper.insert(user);
        return user;
    }

    @Override
    public User findUserByUsername(String username) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);

        List<User> userList = userMapper.selectByExample(example);
        return userList.get(0);
    }
}
