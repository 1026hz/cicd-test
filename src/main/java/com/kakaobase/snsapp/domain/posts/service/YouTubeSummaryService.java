package com.kakaobase.snsapp.domain.posts.service;

import com.kakaobase.snsapp.domain.posts.dto.PostRequestDto;
import com.kakaobase.snsapp.domain.posts.exception.PostException;
import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * YouTube 영상 요약 서비스
 *
 * <p>AI 서버와 통신하여 YouTube 영상의 요약을 처리하는 서비스입니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class YouTubeSummaryService {

    private final WebClient webClient;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    /**
     * YouTube 영상 요약 요청
     *
     * <p>AI 서버에 YouTube URL을 전송하여 영상의 요약본을 받아옵니다.</p>
     *
     * @param youtubeUrl YouTube 영상 URL
     * @return 요약된 내용
     * @throws PostException AI 서버 통신 실패 또는 요약 실패 시
     */
    public String getSummary(String youtubeUrl) {
        log.info("YouTube 요약 요청 시작 - URL: {}", youtubeUrl);

        try {
            // AI 서버 요청 DTO 생성
            PostRequestDto.YouTubeAiRequest request = new PostRequestDto.YouTubeAiRequest(youtubeUrl);
            log.info("YoutubeSummary {}", request.toString());

            // AI 서버에 요약 요청
            PostRequestDto.YouTubeAiResponse response = webClient.post()
                    .uri(aiServerUrl + "/posts/youtube/summary")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(PostRequestDto.YouTubeAiResponse.class)
                    .block(); // 동기식 처리를 위해 block() 사용

            // 응답에서 요약 내용 추출
            String summary = response.data().summary();
            log.info("YouTube 요약 요청 성공");

            return summary;

        } catch (WebClientResponseException e) {
            log.error("AI 서버 통신 실패 - Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new PostException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("YouTube 요약 중 예외 발생", e);
            throw new PostException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}