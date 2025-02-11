package com.xhs.service;

import com.xhs.dto.CommentDto;
import com.xhs.pojo.Comment;

import java.util.List;

public interface CommentService {
    Comment createComment(Integer userId, CommentDto request);
    void deleteComment(Integer userId, Integer commentId);
    List<CommentDto> getCommentsByPostId(
            Integer postId,
            Integer page, // 新增分页参数
            Integer pageSize
    );


    CommentDto getCommentWithUser(Integer id);
}
