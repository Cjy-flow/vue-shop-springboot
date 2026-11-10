package com.cjy.shop.web;

import com.cjy.shop.common.ApiException;
import com.cjy.shop.security.TokenService;
import com.cjy.shop.web.dto.LoginRequest;
import com.cjy.shop.web.dto.LoginResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员登录。
 *
 * 账号密码来自 application.yml 的 shop.admin.*，
 * 课程项目里直接明文比对；真实项目应改为数据库存 BCrypt 密码哈希。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final String adminUsername;
    private final String adminPassword;
    private final TokenService tokenService;

    public AuthController(@Value("${shop.admin.username}") String adminUsername,
                          @Value("${shop.admin.password}") String adminPassword,
                          TokenService tokenService) {
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        if (!adminUsername.equals(request.username()) || !adminPassword.equals(request.password())) {
            log.warn("登录失败，账号：{}", request.username());
            throw ApiException.unauthorized("账号或密码错误，请重新输入");
        }
        String token = tokenService.issue(request.username());
        log.info("管理员 {} 登录成功", request.username());
        return new LoginResponse(token, request.username(), tokenService.getExpiresInSeconds());
    }
}
