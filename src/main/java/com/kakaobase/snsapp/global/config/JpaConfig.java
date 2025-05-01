package com.kakaobase.snsapp.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 관련 설정을 담당하는 설정 클래스입니다.
 * JPA Auditing 기능을 활성화하여 엔티티의 생성 시간과 수정 시간을 자동으로 관리합니다.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // 추가 JPA 설정이 필요한 경우 여기에 구현
}