package com.cjy.shop.security;

import com.cjy.shop.common.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 鉴权拦截器：
 *   - 查询类请求（GET）放行，前台商城无需登录即可浏览
 *   - 写操作（POST/PUT/DELETE）必须带 Authorization: Bearer &lt;token&gt;
 *
 * 登录接口在 WebConfig 里已排除，不会走到这里。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenService tokenService;

    public AuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 跨域预检请求不需要令牌
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 前台只读接口放行
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String header = request.getHeader(AUTH_HEADER);
        String token = (header != null && header.startsWith(BEARER_PREFIX))
                ? header.substring(BEARER_PREFIX.length()).trim()
                : null;

        String username = tokenService.verify(token)
                .orElseThrow(() -> ApiException.unauthorized("登录状态已失效，请重新登录"));

        // 留给后续 Controller / 日志使用
        request.setAttribute("currentUser", username);
        return true;
    }
}
