package com.xhs.service;

import com.xhs.pojo.Like;

public interface LikeService {
    Like createLike(Integer userId, Integer postId);
    void cancelLike(Integer userId, Integer postId);
    boolean isLiked(Integer userId, Integer postId);
}
