package com.kakaobase.snsapp.domain.posts.service;

import com.kakaobase.snsapp.domain.members.entity.Member;
import com.kakaobase.snsapp.domain.members.repository.MemberRepository;
import com.kakaobase.snsapp.domain.posts.dto.BotRequestDto;
import com.kakaobase.snsapp.domain.posts.dto.PostRequestDto;
import com.kakaobase.snsapp.domain.posts.dto.PostResponseDto;
import com.kakaobase.snsapp.domain.posts.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
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
    private final MemberRepository memberRepository;
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

            // 1. 최근 5개 게시글 조회
            List<Post> recentPosts = postService.findByCursor(boardType, 5, null);
            if (recentPosts.size() < 5) {
                log.warn("게시글이 5개 미만입니다. 봇 게시글 생성을 건너뜁니다. - count: {}", recentPosts.size());
                return null;
            }

            // 2. AI 서버 요청 DTO 생성
            BotRequestDto.CreatePostRequest request = createBotRequest(boardType, recentPosts);

            // 3. AI 서버 호출
            BotRequestDto.CreatePostResponse response = callAiServer(request);

            // 4. 봇 게시글 저장
            PostResponseDto.PostCreateResponse createResponse = saveBotPost(response);

            log.info("봇 게시글 생성 완료 - boardType: {}", boardType);
            return createResponse;

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
        List<BotRequestDto.BotPost> botPosts = posts.stream()
                .map(post -> {
                    Member member = memberRepository.findById(post.getMemberId())
                            .orElseThrow(() -> new IllegalStateException("회원을 찾을 수 없습니다. memberId: " + post.getMemberId()));

                    return new BotRequestDto.BotPost(
                            new BotRequestDto.BotUser(
                                    member.getNickname(),
                                    member.getClassName()
                            ),
                            post.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toString(),
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
    private BotRequestDto.CreatePostResponse callAiServer(BotRequestDto.CreatePostRequest request) {
        try {
            return webClient.post()
                    .uri(aiServerUrl + "/posts/bot")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(BotRequestDto.CreatePostResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("AI 서버 요청 실패 - Status: {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("AI 서버 통신 오류", e);
        }
    }

    /**
     * 봇 게시글 저장
     *
     * @param response AI 서버 응답
     * @return 게시글 생성 응답 DTO
     */
    private PostResponseDto.PostCreateResponse saveBotPost(BotRequestDto.CreatePostResponse response) {
        BotRequestDto.ResponseData data = response.data();

        // 게시판 타입 변환
        Post.BoardType boardType;
        try {
            boardType = Post.BoardType.valueOf(data.boardType());
        } catch (IllegalArgumentException e) {
            log.error("잘못된 게시판 타입: {}", data.boardType());
            throw new RuntimeException("잘못된 게시판 타입", e);
        }

        // PostService의 createPost 메서드에 전달할 DTO 생성
        // PostCreateRequestDto는 snake_case 필드명 사용
        PostRequestDto.PostCreateRequestDto createRequest = new PostRequestDto.PostCreateRequestDto(
                data.content(),
                null, // 봇은 이미지 없음
                null                     // 봇은 YouTube URL 없음
        );

        // PostService의 createPost 메서드 재사용
        // postType (소문자)로 전달
        return postService.createPost(createRequest, BotConstants.BOT_MEMBER_ID, boardType.name().toLowerCase());
    }
}