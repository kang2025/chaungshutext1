package com.xhs.controller;

import com.xhs.dto.UserLoginDTO;
import com.xhs.dto.UserRegisterDTO;
import com.xhs.dto.UserUpdateDTO;
import com.xhs.exception.UnauthorizedException;
import com.xhs.pojo.User;
import com.xhs.result.Result;
import com.xhs.service.UserService;
import com.xhs.util.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户控制器类，处理与用户相关的HTTP请求
 */
@RestController
@RequestMapping("/user")
public class UserController {
    /**
     * 用户服务接口，用于执行用户相关的业务逻辑
     */
    private final UserService userService;
    /**
     * JWT工具类，用于处理JWT令牌的生成和解析
     */
    private final JwtUtils jwtUtils;

    /**
     * 构造函数注入UserService和JwtUtils
     *
     * @param userService 用户服务接口
     * @param jwtUtils     JWT工具类
     */
    @Autowired
    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 用户注册接口
     *
     * @param dto 用户注册数据传输对象，包含用户注册所需的信息
     * @return 返回注册结果，成功则返回成功信息
     */
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody UserRegisterDTO dto) {
        try {
            userService.register(dto);
            return Result.success("注册成功");
        } catch (IllegalArgumentException e) {
            return Result.fail(400, e.getMessage());
        }
    }


    /**
     * 用户登录接口
     *
     * @param dto 用户登录数据传输对象，包含用户登录所需的用户名和密码
     * @return 返回登录结果，成功则返回用户令牌
     */
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody UserLoginDTO dto) {
        String token = userService.login(dto);
        if (token == null) {
            return Result.fail(400,"登录失败，Token 生成失败");
        }
        return Result.success(token);
    }


    /**
     * 获取用户信息接口
     *
     * @param token 用户请求时携带的JWT令牌，用于获取用户ID
     * @return 返回用户信息，成功则返回用户对象
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        Integer userId = jwtUtils.getUserId(token);
        User user = userService.getUserById(userId);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result<?> updateUserInfo(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UserUpdateDTO dto) {
        Integer userId = jwtUtils.getUserId(token);
        userService.updateUserInfo(userId, dto.getUsername(), dto.getBio());
        return Result.success();
    }

    // 更换头像
   @PostMapping("/avatar")
    public Result<String> updateAvatar(
            @RequestHeader("Authorization") String token,
            @RequestParam MultipartFile file) {
        Integer userId = jwtUtils.getUserId(token);
        String newAvatar = userService.updateAvatar(userId, file);
        return Result.success(newAvatar);
    }

    // 关注/取关用户
    @PostMapping("/follow/{targetUserId}")
    public Result<?> followUser(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer targetUserId) {
        Integer userId = jwtUtils.getUserId(token);
        userService.followUser(userId, targetUserId);
        return Result.success();
    }
    /**
     * 解析Token并获取用户ID
     *
     * @param token 从请求头中获取的JWT令牌
     * @return 返回用户ID
     */
    private Result<Integer> parseToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Result.fail(401, "缺少有效的 Token");  // 增加错误码
        }
        token = token.substring(7);
        if (jwtUtils.isTokenExpired(token)) {
            return Result.fail(401, "Token 已过期"); // 增加错误码
        }
        Integer userId = jwtUtils.getUserId(token);
        return Result.success(userId);
    }

}
