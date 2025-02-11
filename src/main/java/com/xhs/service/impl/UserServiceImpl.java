package com.xhs.service.impl;
import com.xhs.dto.UserLoginDTO;
import com.xhs.dto.UserRegisterDTO;
import com.xhs.exception.BadRequestException;
import com.xhs.mapper.UserMapper;
import com.xhs.pojo.User;
import com.xhs.service.UserService;
import com.xhs.util.JwtUtils;
import com.xhs.util.OssUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final OssUtil ossUtil;
    // 手机号正则（中国）
    private static final String MOBILE_REGEX = "^1[3-9]\\d{9}$";
    // 邮箱正则
    private static final String EMAIL_REGEX = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    @Autowired
    public UserServiceImpl(UserMapper userMapper,JwtUtils jwtUtils,OssUtil ossUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtils = jwtUtils;
        this.ossUtil = ossUtil;
    }

    @Override
    @Transactional
    public void register(UserRegisterDTO dto) {
        String identifier = dto.getIdentifier();
        boolean isEmail = identifier.contains("@");

        // 检查唯一性
        if (isEmail && userMapper.existsByEmail(identifier) > 0) {
            throw new IllegalArgumentException("邮箱已存在");
        } else if (!isEmail && userMapper.existsByMobile(identifier) > 0) {
            throw new IllegalArgumentException("手机号已存在");
        }

        User user = new User();
        user.setEmail(isEmail ? identifier : null);
        user.setMobile(isEmail ? null : identifier);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCreatedAt(new Date());
        // 核心修复：生成唯一的默认用户名（示例："用户123"）
        user.setUsername("用户" + System.currentTimeMillis()); // 时间戳避免重复
        // 或使用其他生成规则（如UUID前8位）
        userMapper.insert(user);
    }





    @Override
    public String login(UserLoginDTO dto) {
        User user = userMapper.findByMobileOrEmail(dto.getIdentifier());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        String token = jwtUtils.generateToken(user.getId());
        System.out.println("Generated Token: " + token); // 打印生成的 Token
        return token;
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID，用于唯一标识一个用户
     * @return 返回用户对象如果找到，否则返回null
     */
    @Override
    public User getUserById(Integer userId) {
        return userMapper.selectById(userId);
    }
    @Override
    @Transactional
    public void updateUserInfo(Integer userId, String username, String bio) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BadRequestException("用户不存在");

        if (StringUtils.isNotBlank(username)) {
            if (!username.equals(user.getUsername()) && userMapper.existsByUsername(username)) {
                throw new BadRequestException("用户名已存在");
            }
            user.setUsername(username);
        }

        if (bio != null) user.setBio(bio);
        userMapper.update(user);
    }

    @Override
    @Transactional
    public String updateAvatar(Integer userId, MultipartFile file) {
        // 参数校验
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("上传文件不能为空");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("只允许上传图片文件");
        }

        try {
            // 上传到OSS
            String url = ossUtil.upload(file, "user-avatars");

            // 更新数据库
            userMapper.updateAvatar(userId, url);
            return url;
        } catch (IOException e) {
            throw new RuntimeException("头像上传失败", e);
        }
    }


    @Override
    @Transactional
    public void followUser(Integer followerId, Integer targetUserId) {
        // 禁止自关注
        if (followerId.equals(targetUserId)) {
            throw new BadRequestException("不能关注自己");
        }

        // 验证被关注用户存在
        if (userMapper.existsById(targetUserId) == false) {
            throw new BadRequestException("目标用户不存在");
        }

        // 检查已关注状态
        if (userMapper.checkFollowExists(followerId, targetUserId)) {
            userMapper.deleteFollow(followerId, targetUserId); // 取消关注
        } else {
            userMapper.insertFollow(followerId, targetUserId); // 新增关注
        }
    }

    @Override
    public boolean checkFollowStatus(Integer userId, Integer targetUserId) {
        return userMapper.checkFollowExists(userId, targetUserId);
    }



    public String updateAvatar(HttpServletRequest request, MultipartFile file) throws IOException {
        // 获取用户ID（需结合JWTUtils）
        String token = request.getHeader("Authorization");
        Integer userId = jwtUtils.getUserId(token);

        // 格式校验（图片类型）
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("只允许上传图片文件");
        }

        // 使用OssUtil统一上传
        String url = ossUtil.upload(file, "user-avatars");

        // 更新数据库
        userMapper.updateAvatar(userId, url);
        return url;
    }
}
