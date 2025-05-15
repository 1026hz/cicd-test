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
 * AI 요청 전용 Rate Limiting Interceptor
 *
 * <p>/api/ai/** 요청에만 적용됩니다.</p>
 * <p>IP별로 1분에 1건 제한.</p>
 */
@Slf4j
@Component
public class AiRateLimitInterceptor implements HandlerInterceptor {

    // IP별 사용자 Bucket
    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String userKey = getIpKey(request); //  IP 기준 key
        Bucket userBucket = userBuckets.computeIfAbsent(userKey, key -> BucketUtils.createAiBucket());

        if (!userBucket.tryConsume(1)) {
            log.warn("[AI RateLimit] 사용자 IP 요청 제한 초과 - IP: {}, URI: {}", userKey, request.getRequestURI());
            sendRateLimitResponse(response, "AI 요청 제한 초과 (IP당 1분 1건)");
            return false;
        }

        return true; // 통과 → Controller로 전달
    }

    /**
     * 사용자 식별 키 생성 (기본: IP 주소)
     */
    private String getIpKey(HttpServletRequest request) {
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


