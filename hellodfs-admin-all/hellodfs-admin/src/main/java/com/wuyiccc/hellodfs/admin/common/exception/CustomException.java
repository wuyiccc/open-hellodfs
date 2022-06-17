package com.wuyiccc.hellodfs.admin.common.exception;
import com.wuyiccc.hellodfs.admin.common.enumeration.ResponseStatusEnum;

/**
 * @author wuyiccc
 * @date 2020/10/24 10:55
 * 自定义异常
 */
public class CustomException extends RuntimeException {

    private ResponseStatusEnum responseStatus;


    public CustomException(ResponseStatusEnum responseStatus) {
        super("异常状态码: " + responseStatus.getStatus() + "; 异常信息: " + responseStatus.getMsg());
        this.responseStatus = responseStatus;
    }

    public ResponseStatusEnum getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatusEnum responseStatus) {
        this.responseStatus = responseStatus;
    }
}
