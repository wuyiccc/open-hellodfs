package com.wuyiccc.hellodfs.admin.common.exception;


import com.wuyiccc.hellodfs.admin.common.enumeration.ResponseStatusEnum;

/**
 * @author wuyiccc
 * @date 2020/10/24 11:02
 * 抛出自定义异常
 */

public class ThrowException {

    public static void display(ResponseStatusEnum responseStatus) {
        throw new CustomException(responseStatus);
    }
}
