package com.wuyiccc.hellodfs.admin.common.enumeration;

/**
 * @author wuyiccc
 * @date 2020/10/24 17:22
 * 性别枚举
 */

public enum SexEnum {

    /**
     * 女
     */
    WOMAN("0", "女"),
    /**
     * 男
     */
    MAN("1", "男"),
    /**
     * 保密
     */
    SECRET("2", "保密");

    private final String value;
    private final String desc;

    SexEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
