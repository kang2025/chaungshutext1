package com.xhs.mapper;

import com.xhs.dto.CommentDto;
import com.xhs.pojo.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommentMapper {

    @Insert("INSERT INTO comment (post_id, user_id, content, created_at) " +  // 显式包含时间字段
            "VALUES (#{postId}, #{userId}, #{content}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")  // 确保生成ID回填到对象
    int insert(Comment comment);

    @Delete("DELETE FROM comment WHERE id = #{commentId} AND user_id = #{userId}")
    int delete(@Param("commentId") Integer commentId, @Param("userId") Integer userId);

    @Select("SELECT * FROM comment WHERE post_id = #{postId} ORDER BY created_at DESC")
    List<Comment> selectByPostId(Integer postId);

    // 分页联查用户信息（重点！）
    @Select("SELECT c.*, u.username, u.avatar, u.id AS userId " + // 明确字段别名
            "FROM comment c JOIN user u ON c.user_id = u.id " +
            "WHERE c.post_id = #{postId} " +
            "ORDER BY c.created_at DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<CommentDto> selectByPostIdWithUser(
            @Param("postId") Integer postId,
            @Param("offset") Integer offset,   // 兼容Spring分页参数
            @Param("pageSize") Integer pageSize
    );
    // 原子更新评论数（关键事务支持）
    @Update("UPDATE post SET comment_count = comment_count + #{increment} WHERE id = #{postId}")
    int updateCommentCount(
            @Param("postId") Integer postId,
            @Param("increment") int increment  // +1/-1实现增减
    );
    @Select("SELECT c.id, c.content, c.created_at, " +  // 显式列出字段
            "u.id AS userId, u.username, u.avatar " +    // 字段别名避免冲突
            "FROM comment c JOIN user u ON c.user_id = u.id " +
            "WHERE c.id = #{commentId}")
    Optional<CommentDto> selectCommentWithUserById(Integer commentId);
    @Select("SELECT * FROM comment WHERE id = #{commentId}")
    Optional<Comment> selectById(Integer commentId);
}