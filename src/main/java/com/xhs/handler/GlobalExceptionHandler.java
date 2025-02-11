package com.xhs.handler;

import com.xhs.exception.BadRequestException;
import com.xhs.exception.BusinessException;
import com.xhs.exception.UnauthorizedException;
import com.xhs.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 业务异常处理（400）
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    // 参数校验异常处理（400）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("参数校验失败");
        return Result.error(HttpStatus.BAD_REQUEST.value(), errorMsg);
    }

    // 认证异常处理（401）
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<?> handleUnauthorizedException(UnauthorizedException e) {
        return Result.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }

    // 空指针异常处理（500）
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleNullPointerException(NullPointerException e) {
        return Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误");
    }

    // 通用异常处理（500）
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        return Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器繁忙，请稍后再试");
    }
    /**
     * 处理BadRequestException异常的处理器方法
     * 当控制器方法抛出BadRequestException时，本方法将被调用以处理该异常
     *
     * @param ex BadRequestException实例，包含异常的相关信息
     * @return 返回一个表示错误的Result对象，包含错误码400和异常消息
     */
    @ExceptionHandler(BadRequestException.class)
    public Result<?> handleBadRequest(BadRequestException ex) {
        return Result.error(400,ex.getMessage());
    }
}
