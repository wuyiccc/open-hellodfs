package com.wuyiccc.hellodfs.admin.common.pojo.dto;

/**
 * @author wuyiccc
 * @date 2022/6/4 15:17
 */
public class UserCreateDTO {

    private String username;

    private String password;

    private String rePassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }

    @Override
    public String toString() {
        return "UserCreateDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", rePassword='" + rePassword + '\'' +
                '}';
    }
}
