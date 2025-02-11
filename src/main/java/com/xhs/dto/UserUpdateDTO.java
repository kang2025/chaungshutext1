package com.xhs.dto;

import jakarta.validation.constraints.Size;

public class UserUpdateDTO {
    @Size(max = 20, message = "昵称最长20字")
    private String username;

    @Size(max = 150, message = "简介最长150字")
    private String bio;

    // 头像文件单独处理

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
