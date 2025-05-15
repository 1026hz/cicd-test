package com.kakaobase.snsapp.global.config;

import com.kakaobase.snsapp.global.interceptor.AiRateLimitInterceptor;
import com.kakaobase.snsapp.global.interceptor.GlobalRateLimitInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 전역 설정
 *
 * <p>Global RateLimit Interceptor를 등록합니다.</p>
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final GlobalRateLimitInterceptor globalRateLimitInterceptor;
    private final AiRateLimitInterceptor aiRateLimitInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalRateLimitInterceptor)
                .addPathPatterns("/**")                     //모든 요청에 적용
                .excludePathPatterns("/post/*/summary");    //AI 요청은 제외

        registry.addInterceptor(aiRateLimitInterceptor)
                .addPathPatterns("/api/ai/**");
    }


}
