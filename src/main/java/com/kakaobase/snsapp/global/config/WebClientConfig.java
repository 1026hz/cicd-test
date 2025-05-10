package com.kakaobase.snsapp.global.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * WebClient 설정
 *
 * <p>외부 API 통신을 위한 WebClient 빈을 설정합니다.</p>
 */
@Slf4j
@Configuration
public class WebClientConfig {

    /**
     * WebClient 빈 생성
     *
     * <p>AI 서버와의 통신을 위한 WebClient를 설정합니다.</p>
     *
     * @return 설정된 WebClient 인스턴스
     */
    @Bean
    public WebClient webClient() {
        // Exchange 전략 설정 (최대 메모리 사이즈 등)
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024); // 10MB
                    configurer.defaultCodecs().enableLoggingRequestDetails(true);
                })
                .build();

        // HttpClient 설정
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 연결 타임아웃 5초
                .responseTimeout(Duration.ofSeconds(30)) // 응답 타임아웃 30초
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS))
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(exchangeStrategies)
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    /**
     * 요청 로깅 필터
     *
     * @return 요청 로깅을 위한 ExchangeFilterFunction
     */
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("=== Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value ->
                    log.debug("Request Header: {}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    /**
     * 응답 로깅 필터
     *
     * @return 응답 로깅을 위한 ExchangeFilterFunction
     */
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("=== Response Status: {}", clientResponse.statusCode());
            clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value ->
                    log.debug("Response Header: {}={}", name, value)));
            return Mono.just(clientResponse);
        });
    }
}