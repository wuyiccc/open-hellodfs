package com.wuyiccc.hellodfs.admin.common.enumeration;

/**
 * @author wuyiccc
 * @date 2021/2/10 8:32
 * <p>
 * 响应结果枚举，用于提供给UnifyResponse返回给前端的
 */
public enum ResponseStatusEnum {

    /**
     * 操作成功
     */
    SUCCESS(200, true, "操作成功！"),
    /**
     * 操作失败
     */
    FAILED(500, false, "操作失败！"),

    // 50x 51x
    /**
     * 未登录
     */
    UN_LOGIN(501, false, "请登录后再继续操作！"),
    /**
     * 会话失效
     */
    TICKET_INVALID(502, false, "会话失效，请重新登录！"),
    SYSTEM_UNKNOWN_ERROR(561, false, "服务器未知错误"),
    FILE_MAX_SIZE_ERROR(513, false, "文件上传大小超出限制"),
    SYSTEM_DATE_PARSER_ERROR(550, false, "系统错误，日期解析出错！"),
    ;

    /**
     * 响应业务状态
     */
    private final Integer status;
    /**
     * 调用是否成功
     */
    private final Boolean success;
    /**
     * 响应信息
     */
    private final String msg;

    ResponseStatusEnum(Integer status, Boolean success, String msg) {
        this.status = status;
        this.success = success;
        this.msg = msg;
    }

    public Integer getStatus() {
        return status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }
}
