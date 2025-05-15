package com.kakaobase.snsapp.domain.posts.service;

import com.kakaobase.snsapp.domain.posts.converter.PostConverter;
import com.kakaobase.snsapp.domain.posts.dto.BotRequestDto;
import com.kakaobase.snsapp.domain.posts.dto.PostRequestDto;
import com.kakaobase.snsapp.domain.posts.dto.PostResponseDto;
import com.kakaobase.snsapp.domain.posts.entity.Post;
import com.kakaobase.snsapp.global.common.constant.BotConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI 봇의 게시글 관련 서비스
 *
 * <p>게시글이 5개 생성될 때마다 AI 서버에 요청하여 자동으로 봇 게시글을 생성합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotPostService {

    private final PostService postService;
    private final WebClient webClient;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    /**
     * AI 봇 게시글 생성
     *
     * <p>최근 5개 게시글을 기반으로 AI 서버에 요청하여 봇 게시글을 생성합니다.</p>
     *
     * @param boardType 게시판 타입
     * @return 생성된 봇 게시글 응답 (null일 수 있음)
     */
    @Transactional
    public PostResponseDto.PostCreateResponse createBotPost(Post.BoardType boardType) {
        try {
            log.info("봇 게시글 생성 시작 - boardType: {}", boardType);

            // 1. 최근 게시글 조회 (봇 게시글 필터링을 위해 여유있게 10개 조회)
            List<Post> recentPosts = postService.findByCursor(boardType, 10, null);
            log.info("조회된 최근 게시글 수: {}", recentPosts.size());

            // ArrayList로 초기화 (최종 반환 타입은 List 인터페이스)
            List<Post> filteredPosts = new ArrayList<>();

            // 봇이 작성하지 않은 게시글만 필터링
            int botPostCount = 0;
            for (Post post : recentPosts) {
                if (post.getMemberId() != BotConstants.BOT_MEMBER_ID) {
                    filteredPosts.add(post);
                    log.debug("일반 게시글 추가: id={}, content={}, createdAt={}",
                            post.getId(), post.getContent(), post.getCreatedAt());


                    if (filteredPosts.size() == 5) {
                        log.info("5개의 일반 게시글 필터링 완료. 반복 중단");
                        break;
                    }
                } else {
                    botPostCount++;
                    log.debug("봇 게시글 필터링 제외: id={}, content={}", post.getId(), post.getContent());
                }
            }

            log.info("필터링 결과 - 총 게시글: {}, 봇 게시글: {}, 일반 게시글: {}",
                    recentPosts.size(), botPostCount, filteredPosts.size());


            if (filteredPosts.size() < 5) {
                log.warn("게시글이 5개 미만입니다. 봇 게시글 생성을 건너뜁니다. - count: {}", filteredPosts.size());
                return null;
            }

            Collections.reverse(filteredPosts);

            log.debug("역순 정렬 후 게시글 순서(오래된순):");
            for (int i = 0; i < filteredPosts.size(); i++) {
                Post post = filteredPosts.get(i);
                log.debug("  {}. id={}, content={}, createdAt={}",
                        i+1, post.getId(), post.getContent(), post.getCreatedAt());
            }

            log.info("AI에게 전송할 5개 게시글 준비 완료 (오래된순)");


            // 2. AI 서버 요청 DTO 생성
            BotRequestDto.CreatePostRequest request = createBotRequest(boardType, filteredPosts);

            // 3. AI 서버 호출
            BotRequestDto.AiPostResponse aiResponse = callAiServer(request);

            // 4. 봇 게시글 저장 및 클라이언트 응답 생성
            PostResponseDto.PostCreateResponse clientResponse = saveBotPost(aiResponse);

            log.info("봇 게시글 생성 완료 - boardType: {}", boardType);
            return clientResponse;

        } catch (Exception e) {
            log.error("봇 게시글 생성 실패 - boardType: {}", boardType, e);
            // 실패해도 카운터는 리셋되어야 하므로 예외를 전파하지 않음
            return null;
        }
    }

    /**
     * AI 서버 요청 DTO 생성
     *
     * @param boardType 게시판 타입
     * @param posts 최근 게시글 목록
     * @return AI 서버 요청 DTO
     */
    private BotRequestDto.CreatePostRequest createBotRequest(Post.BoardType boardType, List<Post> posts) {
        // 게시글 작성자들의 정보를 한 번에 조회
        Map<Long, Map<String, String>> memberInfoMap = postService.getMemberInfoByPosts(posts);

        List<BotRequestDto.PostDto> botPosts = posts.stream()
                .map(post -> {
                    Map<String, String> memberInfo = memberInfoMap.get(post.getMemberId());
                    if (memberInfo == null) {
                        throw new IllegalStateException("회원 정보를 찾을 수 없습니다. memberId: " + post.getMemberId());
                    }

                    // Member 엔티티에서 className을 가져오는 로직이 필요
                    // 현재 PostService의 getMemberInfo가 className을 포함하지 않을 수 있음
                    String className = memberInfo.getOrDefault("className", "UNKNOWN");

                    return new BotRequestDto.PostDto(
                            new BotRequestDto.UserDto(
                                    memberInfo.get("nickname"),
                                    className
                            ),
                            post.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toString(),
                            post.getContent()
                    );
                })
                .collect(Collectors.toList());

        return new BotRequestDto.CreatePostRequest(boardType.name(), botPosts);
    }

    /**
     * AI 서버 호출
     *
     * @param request AI 서버 요청 DTO
     * @return AI 서버 응답 DTO
     */
    private BotRequestDto.AiPostResponse callAiServer(BotRequestDto.CreatePostRequest request) {
        try {
            return webClient.post()
                    .uri(aiServerUrl + "/posts/bot")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(BotRequestDto.AiPostResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("AI 서버 요청 실패 - Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("AI 서버 통신 오류", e);
        }
    }

    /**
     * 봇 게시글 저장
     *
     * @param aiResponse AI 서버 응답
     * @return 게시글 생성 응답 DTO
     */
    private PostResponseDto.PostCreateResponse saveBotPost(BotRequestDto.AiPostResponse aiResponse) {
        BotRequestDto.AiResponseData data = aiResponse.data();

        PostRequestDto.PostCreateRequestDto requestDto = new PostRequestDto.PostCreateRequestDto(
                data.content(),
                null,
                null
        );

        // AI 응답 데이터를 사용하여 게시글 생성
        Post.BoardType boardType;
        try {
            boardType = Post.BoardType.valueOf(data.boardType());
        } catch (IllegalArgumentException e) {
            log.error("잘못된 게시판 타입: {}", data.boardType());
            throw new RuntimeException("잘못된 게시판 타입", e);
        }


        // PostService를 통해 게시글 생성
        Post socialBotPost = postService.createPost(boardType, requestDto, BotConstants.BOT_MEMBER_ID);

        // 봇 멤버 정보 조회
        Map<String, String> botMemberInfo = postService.getMemberInfo(BotConstants.BOT_MEMBER_ID);

        // PostConverter를 사용하여 응답 생성
        return PostConverter.toPostCreateResponse(socialBotPost, botMemberInfo, null, false);
    }
}