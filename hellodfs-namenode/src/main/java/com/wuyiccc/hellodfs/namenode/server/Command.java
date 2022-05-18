package com.wuyiccc.hellodfs.namenode.server;

/**
 * @author wuyiccc
 * @date 2022/5/18 22:28
 */
public class Command {

    public static final Integer REGISTER = 1;

    public static final Integer REPORT_COMPLETE_STORAGE_INFO = 2;


    private Integer type;

    private String content;

    public Command() {
    }

    public Command(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
