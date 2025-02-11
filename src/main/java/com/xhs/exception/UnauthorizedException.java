package com.xhs.exception;

// 认证异常（如 Token 无效）
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
