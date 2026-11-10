package com.cjy.shop.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

/**
 * 极简签名令牌（HMAC-SHA256），不引入 Spring Security / JWT 依赖。
 *
 * 令牌格式：base64url(用户名|过期时间戳) + "." + base64url(签名)
 *
 * 为什么不用内存 Map 存 token：那样服务一重启，所有已登录的后台页面就会被踢出。
 * 用签名校验则是无状态的——服务重启后旧 token 依然能验通，直到过期为止。
 */
@Component
public class TokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private final SecretKeySpec keySpec;
    private final Duration ttl;

    public TokenService(@Value("${shop.token.secret}") String secret,
                        @Value("${shop.token.ttl-minutes:720}") long ttlMinutes) {
        this.keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
        this.ttl = Duration.ofMinutes(ttlMinutes);
    }

    public long getExpiresInSeconds() {
        return ttl.toSeconds();
    }

    /** 签发令牌 */
    public String issue(String username) {
        long expireAt = Instant.now().plus(ttl).toEpochMilli();
        String payload = username + "|" + expireAt;
        String encodedPayload = ENCODER.encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return encodedPayload + "." + sign(encodedPayload);
    }

    /** 校验令牌，返回用户名；非法或过期返回空 */
    public Optional<String> verify(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        int dot = token.indexOf('.');
        if (dot <= 0 || dot == token.length() - 1) {
            return Optional.empty();
        }
        String encodedPayload = token.substring(0, dot);
        String signature = token.substring(dot + 1);

        // 用 MessageDigest.isEqual 做定长比较，避免时序攻击
        byte[] expected = sign(encodedPayload).getBytes(StandardCharsets.UTF_8);
        byte[] actual = signature.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expected, actual)) {
            return Optional.empty();
        }

        try {
            String payload = new String(DECODER.decode(encodedPayload), StandardCharsets.UTF_8);
            String[] parts = payload.split("\\|");
            if (parts.length != 2) {
                return Optional.empty();
            }
            long expireAt = Long.parseLong(parts[1]);
            if (Instant.now().toEpochMilli() > expireAt) {
                return Optional.empty();
            }
            return Optional.of(parts[0]);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(keySpec);
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return ENCODER.encodeToString(raw);
        } catch (Exception e) {
            throw new IllegalStateException("令牌签名失败", e);
        }
    }
}
