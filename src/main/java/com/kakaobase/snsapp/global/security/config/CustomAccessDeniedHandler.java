package com.kakaobase.snsapp.global.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증은 되었지만 권한이 없는 사용자가 보호된 리소스에 접근할 때 호출되는 핸들러입니다.
 * Spring Security에서 인가(권한) 실패 시 403 Forbidden 응답을 생성합니다.
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 권한이 없는 사용자의 요청을 처리합니다.
     * 403 Forbidden 상태 코드와 함께 에러 메시지를 JSON 형식으로 반환합니다.
     *
     * @param request 현재 요청
     * @param response 응답 객체
     * @param accessDeniedException 접근 거부 예외
     * @throws IOException 응답 작성 중 IO 예외 발생 시
     * @throws ServletException 서블릿 관련 예외 발생 시
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        log.debug("Access denied for request: {}", request.getRequestURI());

        SecurityExceptionHandler.sendErrorResponse(
                response,
                HttpStatus.FORBIDDEN,
                "forbidden",
                "해당 리소스에 접근할 권한이 없습니다."
        );
    }
}