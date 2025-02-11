package com.xhs.service;

public interface NotificationService {
    enum NotificationType { LIKE, COMMENT, FOLLOW }

    void createNotification(Integer sourceUserId, Integer targetId, NotificationType type);
}
