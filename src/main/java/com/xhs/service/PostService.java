package com.xhs.service;

import com.xhs.pojo.Post;
import java.util.List;

public interface PostService {
    Post createPost(Integer userId, String content, String imageUrl);
    Post getPostById(Integer id);
    List<Post> getPostsByPage(Integer page, Integer pageSize);
    void deletePost(Integer postId, Integer userId);
    Post updatePost(Post post);
}

