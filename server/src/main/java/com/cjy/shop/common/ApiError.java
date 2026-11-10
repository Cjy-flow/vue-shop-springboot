package com.cjy.shop.common;

import java.time.Instant;

/**
 * 统一错误响应体，前端 src/api/http.js 会读取 message 字段做提示。
 */
public record ApiError(Instant timestamp, int status, String error, String message, String path) {

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(Instant.now(), status, error, message, path);
    }
}
