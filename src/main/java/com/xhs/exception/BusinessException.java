package com.xhs.exception;

// 业务异常（如用户名已存在）
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
