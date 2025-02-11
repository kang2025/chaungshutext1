package com.xhs.mapper;

import com.xhs.pojo.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface PostMapper {
    @Insert("INSERT INTO post(user_id, content, images, created_at) " +
            "VALUES(#{userId}, #{content}, #{images}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Post post);

    @Select("SELECT * FROM post WHERE id = #{id}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "images", column = "images"),
            @Result(property = "createdAt", column = "created_at")
    })
    Post selectById(Integer id);

    @Select("SELECT * FROM post ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Post> selectPage(@Param("offset") Integer offset,
                          @Param("pageSize") Integer pageSize);

    @Delete("DELETE FROM post WHERE id = #{id} AND user_id = #{userId}")
    int deleteByIdAndUser(@Param("id") Integer id,
                          @Param("userId") Integer userId);

    @Update("UPDATE post SET content = #{content}, image_url = #{imageUrl}, " +
            "update_time = NOW() WHERE id = #{id}")
    int update(Post post);
    @Update("UPDATE post SET like_count = like_count + 1 WHERE id = #{postId}")
    void incrementLikeCount(Integer postId);

    @Update("UPDATE post SET like_count = GREATEST(like_count - 1, 0) WHERE id = #{postId}")
    void decrementLikeCount(Integer postId);
}

