package com.xhs.mapper;

import com.xhs.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    // === 查询方法 ===
    @Select("SELECT * FROM user WHERE mobile = #{mobile}")
    User findByMobile(String mobile);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(String email);

    User selectById(Integer id);

    // === 存在性检查 ===
    int existsByMobile(String mobile);
    int existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsById(Integer id);
    User findByMobileOrEmail(String identifier);


    int insert(User user);

    void update(User user);
    void updateAvatar(@Param("userId") Integer userId, @Param("avatar") String avatar);
    boolean checkFollowExists(@Param("userId") Integer userId,
                              @Param("followedUserId") Integer followedUserId);

    // 方法改造建议

    void insertFollow(@Param("userId") Integer userId, @Param("followedUserId") Integer followedUserId);


    void deleteFollow(@Param("userId") Integer userId,
                      @Param("followedUserId") Integer followedUserId);
}
