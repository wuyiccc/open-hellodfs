package com.wuyiccc.hellodfs.admin.common;

import com.wuyiccc.hellodfs.admin.common.enumeration.ResponseStatusEnum;

import java.util.Map;

/**
 * @author wuyiccc
 * @date 2021/2/10 8:22
 * 统一响应结构
 */

public class CommonJSONResult {

    /**
     * 响应业务状态码
     */
    private Integer status;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     *响应数据
     */
    private Object data;

    public CommonJSONResult() {
    }

    public CommonJSONResult(ResponseStatusEnum responseStatusEnum) {
        this.status = responseStatusEnum.getStatus();
        this.msg = responseStatusEnum.getMsg();
        this.success = responseStatusEnum.getSuccess();
    }

    public CommonJSONResult(ResponseStatusEnum responseStatusEnum, Object data) {
        this(responseStatusEnum);
        this.data = data;
    }

    public CommonJSONResult(ResponseStatusEnum responseStatusEnum, String msg) {
        this.status = responseStatusEnum.getStatus();
        this.msg = msg;
        this.success = responseStatusEnum.getSuccess();
    }

    public CommonJSONResult(Object data) {
        this.status = ResponseStatusEnum.SUCCESS.getStatus();
        this.msg = ResponseStatusEnum.SUCCESS.getMsg();
        this.success = ResponseStatusEnum.SUCCESS.getSuccess();
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 成功返回带有数据的信息
     * @param data
     * @return
     */
    public static CommonJSONResult ok(Object data) {
        return new CommonJSONResult(data);
    }

    /**
     * 成功返回不带数据的信息
     * @return
     */
    public static CommonJSONResult ok() {
        return new CommonJSONResult(ResponseStatusEnum.SUCCESS);
    }
    /**
     * 错误返回，直接调用error方法即可，当然也可以在ResponseStatusEnum中自定义错误后再返回也都可以
     * @return
     */
    public static CommonJSONResult error() {
        return new CommonJSONResult(ResponseStatusEnum.FAILED);
    }


    /**
     * 错误返回，map中包含了多条错误信息，可以用于表单验证，把错误统一的全部返回出去
     * @param map
     * @return
     */
    public static CommonJSONResult errorMap(Map map) {
        return new CommonJSONResult(ResponseStatusEnum.FAILED, map);
    }

    /**
     * 错误返回，直接返回错误的消息
     * @param msg
     * @return
     */
    public static CommonJSONResult errorMsg(String msg) {
        return new CommonJSONResult(ResponseStatusEnum.FAILED, msg);
    }

    /**
     * 错误返回，token异常，一些通用的可以在这里统一定义
     * @return
     */
    public static CommonJSONResult errorTicket() {
        return new CommonJSONResult(ResponseStatusEnum.TICKET_INVALID);
    }

    /**
     * 自定义错误范围，需要传入一个自定义的枚举，可以到[ResponseStatusEnum.java]中自定义后再传入
     * @param responseStatus
     * @return
     */
    public static CommonJSONResult errorCustom(ResponseStatusEnum responseStatus) {
        return new CommonJSONResult(responseStatus);
    }
    public static CommonJSONResult exception(ResponseStatusEnum responseStatus) {
        return new CommonJSONResult(responseStatus);
    }

    @Override
    public String toString() {
        return "WantYouJSONResult{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", success=" + success +
                ", data=" + data +
                '}';
    }
}
