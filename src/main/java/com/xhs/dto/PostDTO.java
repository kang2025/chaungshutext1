package com.xhs.dto;

import jakarta.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;


public class PostDTO {
    @NotBlank(message = "内容不能为空")
    private MultipartFile[] images; // 适配多图上传
    private String content;

    public MultipartFile[] getImages() {
        return images;
    }
    public void setImages(MultipartFile[] images) {
        this.images = images;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}

