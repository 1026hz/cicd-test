package com.kakaobase.snsapp.global.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaobase.snsapp.global.common.dto.response.CustomResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 보안 관련 예외 발생 시 JSON 형식의 에러 응답을 생성하는 유틸리티 클래스입니다.
 * Spring Security의 AuthenticationEntryPoint와 AccessDeniedHandler에서 사용됩니다.
 */
@Slf4j
public class SecurityExceptionHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * HTTP 응답에 JSON 형식의 에러 메시지를 작성합니다.
     *
     * @param response HTTP 응답 객체
     * @param status HTTP 상태 코드
     * @param error 에러 코드 문자열
     * @param message 에러 메시지
     * @throws IOException 응답 작성 중 IO 예외 발생 시
     */
    public static void sendErrorResponse(
            HttpServletResponse response,
            HttpStatus status,
            String error,
            String message
    ) throws IOException {
        // HTTP 상태 코드 설정
        response.setStatus(status.value());

        // Content-Type 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 에러 응답 객체 생성
        CustomResponse<Void> errorResponse = CustomResponse.failure(error, message);

        // ObjectMapper를 사용하여 응답 직렬화 및 작성
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);

        log.debug("Security error response sent: status={}, error={}, message={}", status, error, message);
    }
}