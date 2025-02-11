package com.xhs.controller;

import com.xhs.mapper.NotificationMapper;
import com.xhs.pojo.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationMapper notificationMapper;

    // 获取通知列表
    @GetMapping("")
    public ResponseEntity<List<Notification>> getNotifications(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        int offset = page * pageSize;
        List<Notification> list = notificationMapper.selectByUserId(userId, offset, pageSize);
        return ResponseEntity.ok(list);
    }

    // 获取未读通知数量
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(@RequestParam Integer userId) {
        return ResponseEntity.ok(notificationMapper.countUnread(userId));
    }

    // 标记为已读
    @PatchMapping("/mark-read")
    public ResponseEntity<Void> markAsRead(@RequestBody List<Integer> notificationIds) {
        notificationMapper.markAsRead(notificationIds);
        return ResponseEntity.ok().build();
    }
}
