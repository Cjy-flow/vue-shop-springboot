package com.cjy.shop.common;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：把各类异常收敛成统一的 JSON 结构，
 * 前端只需读 message 就能给出人话提示。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 业务异常（商品不存在、参数不合法等） */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException ex, HttpServletRequest request) {
        return build(ex.getStatus(), ex.getMessage(), request);
    }

    /** @Valid 校验失败：取第一条错误信息返回，避免前端展示一长串 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                    HttpServletRequest request) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "请求参数不合法";
        return build(HttpStatus.BAD_REQUEST, message, request);
    }

    /** 请求体不是合法 JSON */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex,
                                                     HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "请求体格式不正确，应为 JSON", request);
    }

    /** 兜底：未预料的异常，日志留全栈，返回给前端的只给概括信息 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex, HttpServletRequest request) {
        log.error("未处理的异常: {} {}", request.getMethod(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误，请稍后重试", request);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest request) {
        ApiError body = ApiError.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
