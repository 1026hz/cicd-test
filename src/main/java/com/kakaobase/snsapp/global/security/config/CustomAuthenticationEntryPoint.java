package com.kakaobase.snsapp.global.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출되는 진입점입니다.
 * Spring Security에서 인증 실패 시 401 Unauthorized 응답을 생성합니다.
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 인증되지 않은 사용자의 요청을 처리합니다.
     * 401 Unauthorized 상태 코드와 함께 에러 메시지를 JSON 형식으로 반환합니다.
     *
     * @param request 현재 요청
     * @param response 응답 객체
     * @param authException 인증 예외
     * @throws IOException 응답 작성 중 IO 예외 발생 시
     * @throws ServletException 서블릿 관련 예외 발생 시
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        log.debug("Unauthorized access attempt: {}", request.getRequestURI());

        SecurityExceptionHandler.sendErrorResponse(
                response,
                HttpStatus.UNAUTHORIZED,
                "unauthorized",
                "유효한 토큰이 필요합니다."
        );
    }
}