package com.xhs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentDto {
    @NotNull(message = "帖子ID不能为空")
    private Integer postId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    public @NotNull(message = "帖子ID不能为空") Integer getPostId() {
        return postId;
    }

    public @NotBlank(message = "评论内容不能为空") String getContent() {
        return content;
    }

    public void setContent(@NotBlank(message = "评论内容不能为空") String content) {
        this.content = content;
    }
    public CommentDto setPostId(Integer postId) {
        this.postId = postId;
        return this; // 添加链式调用支持
    }
}
