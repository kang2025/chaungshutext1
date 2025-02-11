package com.xhs.mapper;

import com.xhs.pojo.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {

    // 插入新通知
    @Insert("INSERT INTO notification(type, user_id, source_user_id, post_id) " +
            "VALUES (#{type}, #{userId}, #{sourceUserId}, #{postId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);

    // 获取用户通知列表（带分页）
    @Select("SELECT n.*, u.username as sender_name, u.avatar as sender_avatar " +
            "FROM notification n JOIN user u ON n.source_user_id = u.id " +
            "WHERE n.user_id = #{userId} ORDER BY n.created_at DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<Notification> selectByUserId(
            @Param("userId") Integer userId,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );

    // 标记通知为已读（支持单条或批量）
    @Update({"<script>",
            "UPDATE notification SET is_read = 1 WHERE id IN ",
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"})
    int markAsRead(@Param("ids") List<Integer> ids);

    // 获取用户未读通知数量
    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND is_read = 0")
    int countUnread(Integer userId);
}

