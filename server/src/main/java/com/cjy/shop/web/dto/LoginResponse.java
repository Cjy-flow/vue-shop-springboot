package com.cjy.shop.web.dto;

/**
 * 登录成功响应。
 *
 * @param token            访问令牌，前端存到 localStorage 的 adminToken
 * @param username         登录账号
 * @param expiresInSeconds 有效期（秒）
 */
public record LoginResponse(String token, String username, long expiresInSeconds) {
}
