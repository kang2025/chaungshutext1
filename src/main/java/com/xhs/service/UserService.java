// src/main/java/com/xhs/service/UserService.java
package com.xhs.service;

import com.xhs.dto.UserLoginDTO;
import com.xhs.dto.UserRegisterDTO;
import com.xhs.pojo.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    /**
     * 用户注册
     * @param dto 注册请求参数
     * @throws IllegalArgumentException 当参数校验失败或账号已存在时抛出
     */
    void register(UserRegisterDTO dto);
    User getUserById(Integer userId);
    /**
     * 用户登录
     * @param dto 登录请求参数
     * @return JWT令牌
     * @throws IllegalArgumentException 当账号或密码错误时抛出
     */
    String login(UserLoginDTO dto);

    void updateUserInfo(Integer userId, String username, String bio);

    String updateAvatar(Integer userId, MultipartFile file);

    void followUser(Integer followerId, Integer targetUserId);

    boolean checkFollowStatus(Integer userId, Integer targetUserId);
}
