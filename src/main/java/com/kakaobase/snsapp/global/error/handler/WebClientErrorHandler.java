package com.kakaobase.snsapp.global.error.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaobase.snsapp.global.error.exception.AiServerExcepiton;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
public class WebClientErrorHandler {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Function<ClientResponse, Mono<? extends Throwable>> aiSummaryErrorHandler() {
        return clientResponse ->
                clientResponse.bodyToMono(String.class).map(json -> {
                    try {
                        JsonNode root = mapper.readTree(json);
                        String error = root.path("error").asText();
                        String message = root.path("message").asText();

                        return new AiServerExcepiton(error, message);

                    } catch (Exception e) {
                        log.error("Ai서버에서 받은 에러 응답 JSON파싱 실패: ", e);
                        return new AiServerExcepiton("json_parsing_error", "AI 응답 파싱 실패");
                    }
                });
    }
}


