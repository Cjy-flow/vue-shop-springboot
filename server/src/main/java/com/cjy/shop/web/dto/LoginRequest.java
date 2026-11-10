package com.cjy.shop.web.dto;

import jakarta.validation.constraints.NotBlank;

/** 管理员登录请求体 */
public record LoginRequest(
        @NotBlank(message = "请输入账号") String username,
        @NotBlank(message = "请输入密码") String password
) {
}
