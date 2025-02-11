package com.xhs.dto;


import jakarta.validation.constraints.NotBlank;

public class UserLoginDTO {
    @NotBlank(message = "登录标识不能为空")
    private String identifier; // 手机号或邮箱

    @NotBlank(message = "密码不能为空")
    private String password;

    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
