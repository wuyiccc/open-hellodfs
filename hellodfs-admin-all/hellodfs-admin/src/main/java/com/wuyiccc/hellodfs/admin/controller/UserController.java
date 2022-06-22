package com.wuyiccc.hellodfs.admin.controller;

import com.wuyiccc.hellodfs.admin.common.CommonJSONResult;
import com.wuyiccc.hellodfs.admin.common.pojo.User;
import com.wuyiccc.hellodfs.admin.common.pojo.dto.UserCreateDTO;
import com.wuyiccc.hellodfs.admin.common.pojo.dto.UserLoginDTO;
import com.wuyiccc.hellodfs.admin.common.pojo.vo.UserLoginVO;
import com.wuyiccc.hellodfs.admin.common.util.RedisUtil;
import com.wuyiccc.hellodfs.admin.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author wuyiccc
 * @date 2022/6/4 15:30
 */
@RestController
@RequestMapping("/hellodfsAdmin/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @PostMapping("/createUser")
    public CommonJSONResult createUser(@RequestBody UserCreateDTO userCreateDTO) {
        if (userCreateDTO.getPassword() == null || !userCreateDTO.getPassword().equals(userCreateDTO.getRePassword())) {
            return CommonJSONResult.errorMsg("密码与确认密码不一致, 请重新输入");
        }
        User user = userService.createUser(userCreateDTO);
        return CommonJSONResult.ok(user);
    }

    @PostMapping("/login")
    public CommonJSONResult login(@RequestBody UserLoginDTO userLoginDTO) {
        if (StringUtils.isBlank(userLoginDTO.getUsername()) && StringUtils.isBlank(userLoginDTO.getPassword())) {
            return CommonJSONResult.errorMsg("用户名/密码不能为空");
        }

        User user = userService.findUserByUsername(userLoginDTO.getUsername());
        boolean checkPassword = BCrypt.checkpw(userLoginDTO.getPassword(), user.getPassword());

        if (!checkPassword) {
            return CommonJSONResult.errorMsg("用户密码不正确, 请重新输入");
        }

        String token = UUID.randomUUID().toString().trim();

        redisUtil.set("userId:" + user.getId(), token);

        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtils.copyProperties(user, userLoginVO);
        userLoginVO.setToken(token);

        return CommonJSONResult.ok(userLoginVO);
    }

    @PostMapping("/logout")
    public CommonJSONResult logout(HttpServletRequest request) {
        String userId = request.getHeader("userId");
        if (StringUtils.isBlank(userId)) {
            return CommonJSONResult.errorMsg("用戶id不能为空");
        }

        redisUtil.del("userId:" + userId);
        return CommonJSONResult.ok("退出登录成功");
    }

}
