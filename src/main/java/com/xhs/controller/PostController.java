package com.xhs.controller;

import com.xhs.dto.CommentDto;
import com.xhs.pojo.Comment;
import com.xhs.pojo.Like;
import com.xhs.pojo.Post;
import com.xhs.result.Result;
import com.xhs.service.CommentService;
import com.xhs.service.LikeService;
import com.xhs.service.PostService;
import com.xhs.util.JwtUtils;
import com.xhs.util.OssUtil;
import jakarta.validation.Valid;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final JwtUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    @Autowired
    private OssUtil ossUtil;

    @Autowired
    public PostController(PostService postService, CommentService commentService, LikeService likeService, JwtUtils jwtUtils) {
        this.postService = postService;
        this.commentService = commentService;
        this.likeService = likeService;
        this.jwtUtils = jwtUtils;
    }

    // 发布帖子（带多图上传）
// PostController.java
    @PostMapping
    public Result<Post> createPost(
            @RequestHeader("Authorization") String token,
            @RequestParam("content") String content,
            @RequestParam(value = "images", required = false) MultipartFile[] images) { // [!code focus]

        Integer userId = jwtUtils.getUserIdFromHeader(token.replace("Bearer ", "")); // [!code ++]

        List<String> validImages = new ArrayList<>();
        if (images != null) {
            for (MultipartFile image : images) {
                if (!image.isEmpty() && image.getContentType() != null
                        && image.getContentType().startsWith("image/")) { // [!code focus]
                    try {
                        String url = ossUtil.upload(image, "posts");
                        validImages.add(url);
                    } catch (IOException e) {
                        logger.error(() -> "图片上传失败: " + e.getMessage(), e);
                    }
                }
            }
        }

        String imageUrls = String.join(",", validImages);
        Post createdPost = postService.createPost(userId, content, imageUrls);
        return Result.success(createdPost);
    }


    // 获取帖子详情
    @GetMapping("/{id}")
    public Result<Post> getPostById(@PathVariable Integer id) {
        Post post = postService.getPostById(id);
        return Result.success(post);
    }

    // 分页获取帖子
    @GetMapping
    public Result<List<Post>> getPostsByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        List<Post> posts = postService.getPostsByPage(page, pageSize);
        return Result.success(posts);
    }

    // 删除帖子
    @DeleteMapping("/{id}")
    public Result<?> deletePost(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id) {

        Integer userId = jwtUtils.getUserIdFromHeader(token);
        postService.deletePost(id, userId);
        return Result.success("删除成功");
    }

    // 发布评论
    @PostMapping("/{postId}/comment")
    public Result<CommentDto> createComment(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer postId, // 确保路径参数与DTO参数的一致性
            @Valid @RequestBody CommentDto commentDto) {

        Integer userId = jwtUtils.getUserIdFromHeader(token);

        // 直接使用Service层的createComment方法
        Comment createdComment = commentService.createComment(
                userId, // 用户ID
                commentDto.setPostId(postId) // 设置DTO的postID为路径参数
        );

        return Result.success(commentService.getCommentWithUser(createdComment.getId()));
    }



    // 点赞帖子
    @PostMapping("/{postId}/like")
    public Result<Like> createLike(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer postId) {

        Integer userId = jwtUtils.getUserIdFromHeader(token);
        Like like = likeService.createLike(userId, postId);
        return Result.success(like);
    }

    // 图片上传处理私有方法
    private String processImageUpload(MultipartFile[] images) {
        List<String> urlList = new ArrayList<>();
        if (images != null) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        // 直接调用OSS工具类，"posts"为存储目录
                        String url = ossUtil.upload(image, "posts");
                        urlList.add(url);
                    } catch (IOException e) {
                        throw new RuntimeException("图片上传服务异常");
                    }
                }
            }
        }
        return String.join(",", urlList);
    }
}
