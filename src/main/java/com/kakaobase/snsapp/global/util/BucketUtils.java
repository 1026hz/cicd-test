package com.kakaobase.snsapp.global.util;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.Bucket4j;


import java.time.Duration;

/**
 * Bucket4j Bucket 생성 유틸리티 클래스
 *
 * <p>전역 Bucket, 사용자 Bucket, AI Bucket 등을 중앙에서 일괄 관리.</p>
 */
public class BucketUtils {

    private BucketUtils() {
        // 유틸 클래스 → 인스턴스 생성 방지
    }

    /**
     * 서버 전체 요청을 제한하는 Global Bucket
     * 예: 1분에 1000건
     */
    public static Bucket createGlobalBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(
                        1000,                                        // capacity: 1000 requests
                        Refill.greedy(1000, Duration.ofMinutes(1))  // refill: 1분마다 1000개
                ))
                .build();
    }

    /**
     * UserID별 요청을 제한하는 User Bucket
     * 1분에 10건
     */
    public static Bucket createIpBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(
                        10,                                         // capacity: 10 requests
                        Refill.greedy(10, Duration.ofMinutes(1))    // refill: 1분마다 10개
                ))
                .build();
    }

    /**
     * AI 요청을 위한 별도 Bucket
     * 1분에 5건
     */
    public static Bucket createAiBucket() {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(
                        5,                                          // capacity: 5 requests
                        Refill.greedy(1, Duration.ofMinutes(1))     // refill: 1분마다 1개
                ))
                .build();
    }
}
