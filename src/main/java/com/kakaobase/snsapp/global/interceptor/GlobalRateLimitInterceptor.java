package com.kakaobase.snsapp.global.interceptor;

import com.kakaobase.snsapp.global.util.BucketUtils;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 서버 전역 + 사용자별 요청 Rate Limiting Interceptor
 */
@Slf4j
@Component
public class GlobalRateLimitInterceptor implements HandlerInterceptor {

    // 전역 Bucket
    private final Bucket globalBucket = BucketUtils.createGlobalBucket();

    // 사용자별 Bucket (IP 기반 개별 요청 제한)
    private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 전역 Rate Limit 체크
        if (!globalBucket.tryConsume(1)) {
            log.warn("[RateLimit] 전역 요청 제한 초과 - URI: {}", request.getRequestURI());
            sendRateLimitResponse(response, "서버 전체 요청 제한 초과");
            return false;
        }

        // ✅ 2단계: 사용자 Rate Limit 체크
        String userKey = getUserKey(request);  // 기본은 IP 기준
        Bucket userBucket = ipBuckets.computeIfAbsent(userKey, key -> BucketUtils.createIpBucket());

        if (!userBucket.tryConsume(1)) {
            log.warn("[RateLimit] 사용자 요청 제한 초과 - IP: {}, URI: {}", userKey, request.getRequestURI());
            sendRateLimitResponse(response, "사용자 요청 제한 초과");
            return false;
        }

        return true;  // ✅ 둘 다 통과 → 요청 허용
    }

    /**
     * 사용자 식별 키 생성 (기본: IP 주소)
     * 추후 UserID, Token 등으로 변경 가능
     */
    private String getUserKey(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    /**
     * 요청 제한 응답 반환 (429 Too Many Requests)
     */
    private void sendRateLimitResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}

