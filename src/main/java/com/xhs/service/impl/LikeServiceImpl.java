package com.xhs.service.impl;

import com.xhs.mapper.LikeMapper;
import com.xhs.mapper.PostMapper;
import com.xhs.pojo.Like;
import com.xhs.service.LikeService;
import com.xhs.service.NotificationService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service

public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;
    private final PostMapper postMapper;
    private final NotificationService notificationService;
    @Autowired
    public LikeServiceImpl(
            LikeMapper likeMapper,
            PostMapper postMapper,
            NotificationService notificationService
    ) {
        this.likeMapper = likeMapper;
        this.postMapper = postMapper;
        this.notificationService = notificationService;
    }
    @Override
    @Transactional
    public Like createLike(Integer userId, Integer postId) {
        // 1. 检查是否已点赞
        Like existing = likeMapper.findByUserAndPost(userId, postId);
        if (existing != null) {
            throw new RuntimeException("不可重复点赞");
        }

        // 2. 创建点赞记录
        Like like = new Like();
        like.setUserId(userId);
        like.setPostId(postId);
        likeMapper.insert(like);

        // 3. 更新帖子点赞数
        postMapper.incrementLikeCount(postId);

        // 4. 发送通知
        notificationService.createNotification(userId, postId, NotificationService.NotificationType.LIKE);

        return like;
    }

    @Override
    @Transactional
    public void cancelLike(Integer userId, Integer postId) {
        int rows = likeMapper.delete(userId, postId);
        if (rows > 0) {
            postMapper.decrementLikeCount(postId);
        }
    }

    @Override
    public boolean isLiked(Integer userId, Integer postId) {
        return likeMapper.findByUserAndPost(userId, postId) != null;
    }
}
