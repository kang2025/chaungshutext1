package com.xhs.service.impl;

import com.xhs.exception.BusinessException;
import com.xhs.mapper.PostMapper;
import com.xhs.pojo.Post;
import com.xhs.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    @Autowired
    public PostServiceImpl(PostMapper postMapper) {
        this.postMapper = postMapper;
    }

    @Override
    @Transactional
    public Post createPost(Integer userId, String content, String images) { // 参数名改为images
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setImages(images);
        postMapper.insert(post);
        return postMapper.selectById(post.getId());
    }

    @Override
    public Post getPostById(Integer id) {
        return postMapper.selectById(id);
    }

    @Override
    public List<Post> getPostsByPage(Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        return postMapper.selectPage(offset, pageSize);
    }

    @Override
    @Transactional
    public void deletePost(Integer postId, Integer userId) {
        int affectedRows = postMapper.deleteByIdAndUser(postId, userId);
        if (affectedRows == 0) {
            throw new BusinessException("删除失败：帖子不存在或没有权限");
        }
    }

    @Override
    @Transactional
    public Post updatePost(Post post) {
        postMapper.update(post);
        return postMapper.selectById(post.getId());
    }
}

