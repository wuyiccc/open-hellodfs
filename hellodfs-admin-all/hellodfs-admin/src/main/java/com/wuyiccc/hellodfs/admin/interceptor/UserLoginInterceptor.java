package com.wuyiccc.hellodfs.admin.interceptor;

import com.wuyiccc.hellodfs.admin.common.enumeration.ResponseStatusEnum;
import com.wuyiccc.hellodfs.admin.common.exception.ThrowException;
import com.wuyiccc.hellodfs.admin.common.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wuyiccc
 * @date 2021/2/14 17:20
 */
public class UserLoginInterceptor implements HandlerInterceptor {


    private Logger logger = LoggerFactory.getLogger(UserLoginInterceptor.class);

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        String token = request.getHeader("token");
        boolean login = verifyUserToken(userId, token);
        return login;
    }

    private boolean verifyUserToken(String userId, String token) {
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(token)) {
            String redisToken = redisUtil.get("userId:" + userId);
            if (token.equals(redisToken)) {
                return true;
            }
        }
        ThrowException.display(ResponseStatusEnum.UN_LOGIN);
        return false;
    }

}
