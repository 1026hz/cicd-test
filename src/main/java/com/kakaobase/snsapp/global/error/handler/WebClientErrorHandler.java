package com.kakaobase.snsapp.global.error.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaobase.snsapp.global.error.exception.AiServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
public class WebClientErrorHandler {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static AiServerException parseAiServerException(WebClientResponseException e) {
        try {
            JsonNode root = mapper.readTree(e.getResponseBodyAsString());
            String errorCode = root.path("error").asText(null);
            String message = root.path("message").asText(null);

            if (errorCode == null || message == null) {
                log.warn("AI 서버 에러 응답에서 필요한 필드를 찾지 못했습니다: {}", e.getResponseBodyAsString());
                return new AiServerException("invalid_ai_response", "AI 서버 에러 응답 파싱 실패");
            }

            return new AiServerException(errorCode, message, e);
        } catch (Exception error) {
            log.error("AI 서버 에러 응답 JSON 파싱 실패", error);
            return new AiServerException("json_parsing_error", "AI 응답 파싱 중 오류 발생");
        }
    }

}


