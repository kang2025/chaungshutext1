package com.xhs.service.impl;

import com.xhs.dto.CommentDto;
import com.xhs.mapper.CommentMapper;
import com.xhs.pojo.Comment;
import com.xhs.service.CommentService;
import com.xhs.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final NotificationService notificationService;

    @Autowired
    public CommentServiceImpl(CommentMapper commentMapper,
                              NotificationService notificationService) {
        this.commentMapper = commentMapper;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Comment createComment(Integer userId, CommentDto dto) {
        // 1. 构建带时间戳的评论对象
        Comment comment = new Comment();
        comment.setPostId(dto.getPostId());
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setCreatedAt(new Date()); // 改用更规范的LocalDateTime

        // 2. 插入评论
        int insertResult = commentMapper.insert(comment);
        if (insertResult == 0) {
            throw new RuntimeException("评论创建失败"); // 添加操作结果校验
        }

        // 3. 更新帖子评论数（关键新增！！！）
        commentMapper.updateCommentCount(dto.getPostId(), 1); // 计数+1

        // 4. 异步发送通知（建议添加@Async注解）
        notificationService.createNotification(
                userId,
                comment.getPostId(),
                NotificationService.NotificationType.COMMENT
        );

        return comment;
    }

    @Override
    @Transactional
    public void deleteComment(Integer userId, Integer commentId) {
        // 1. 先获取评论信息（注意：需要新增selectById方法）
        Comment comment = commentMapper.selectById(commentId)  // 直接获取Comment实体
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该评论");
        }
        // 3. 执行删除
        int rows = commentMapper.delete(commentId, userId);
        if (rows == 0) {
            throw new RuntimeException("删除失败");
        }

        // 4. 更新统计数（关键新增！！！）
        commentMapper.updateCommentCount(comment.getPostId(), -1); // 计数-1
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Integer postId, Integer page, Integer pageSize) {
        // 分页参数计算（防御性编程）
        page = (page == null || page < 1) ? 1 : page;
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        int offset = (page - 1) * pageSize;

        return commentMapper.selectByPostIdWithUser(postId, offset, pageSize);
    }

    @Override
    public CommentDto getCommentWithUser(Integer id) {
        return commentMapper.selectCommentWithUserById(id)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
    }

    public Comment getById(Integer commentId) {
        return commentMapper.selectById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
    }

}
