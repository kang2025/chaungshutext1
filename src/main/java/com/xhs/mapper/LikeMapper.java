package com.xhs.mapper;

import com.xhs.pojo.Like;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LikeMapper {
    @Insert("INSERT INTO `like` (user_id, post_id) VALUES (#{userId}, #{postId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Like like);

    @Delete("DELETE FROM `like` WHERE user_id = #{userId} AND post_id = #{postId}")
    int delete(@Param("userId") Integer userId, @Param("postId") Integer postId);

    @Select("SELECT COUNT(*) FROM `like` WHERE post_id = #{postId}")
    int countByPostId(Integer postId);

    @Select("SELECT * FROM `like` WHERE user_id = #{userId} AND post_id = #{postId}")
    Like findByUserAndPost(@Param("userId") Integer userId, @Param("postId") Integer postId);
}