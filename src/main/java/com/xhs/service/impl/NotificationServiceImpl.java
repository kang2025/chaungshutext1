package com.xhs.service.impl;

import com.xhs.mapper.NotificationMapper;
import com.xhs.pojo.Notification;
import com.xhs.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationServiceImpl(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }
    @Override
    public void createNotification(Integer sourceUserId, Integer targetId, NotificationType type) {
        Notification notification = new Notification();
        notification.setUserId(getTargetUserId(targetId, type)); // 需要根据不同类型获取目标用户ID
        notification.setType(type);
        notification.setSourceUserId(sourceUserId);
        notification.setPostId(type == NotificationType.LIKE ? targetId : null);
        notificationMapper.insert(notification);
    }

    private Integer getTargetUserId(Integer targetId, NotificationType type) {
        // 实现逻辑根据业务需求编写
        return targetId; // 假设targetId就是目标用户ID（实际需结合业务逻辑）
    }
}
