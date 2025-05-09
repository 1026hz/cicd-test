package com.kakaobase.snsapp.domain.posts.controller;

import com.kakaobase.snsapp.domain.posts.converter.PostConverter;
import com.kakaobase.snsapp.domain.posts.dto.PostRequestDto;
import com.kakaobase.snsapp.domain.posts.dto.PostResponseDto;
import com.kakaobase.snsapp.domain.posts.entity.Post;
import com.kakaobase.snsapp.domain.posts.exception.PostErrorCode;
import com.kakaobase.snsapp.domain.posts.exception.PostException;
import com.kakaobase.snsapp.domain.posts.service.PostLikeService;
import com.kakaobase.snsapp.domain.posts.service.PostService;
import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import com.kakaobase.snsapp.global.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 게시글 관련 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "게시글 API", description = "게시글 CRUD 및 좋아요 기능을 제공하는 API")
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;

    /**
     * 게시글 목록을 조회합니다.
     * 커서 기반 페이지네이션을 적용합니다.
     */
    @GetMapping("/{postType}")
    @Operation(summary = "게시글 목록 조회", description = "게시판 유형별로 게시글 목록을 조회합니다.")
    @PreAuthorize("@accessChecker.hasAccessToBoard(#postType)")
    public ResponseEntity<PostResponseDto.PostListResponse> getPosts(
            @Parameter(description = "게시판 유형") @PathVariable String postType,
            @Parameter(description = "한 페이지에 표시할 게시글 수") @RequestParam(defaultValue = "12") int limit,
            @Parameter(description = "마지막으로 조회한 게시글 ID") @RequestParam(required = false) Long cursor) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = null;
        List<Long> followingIds = List.of();

        // 유효성 검증
        if (limit < 1) {
            throw new PostException(GeneralErrorCode.INVALID_QUERY_PARAMETER, "limit", "limit는 1 이상이어야 합니다.");
        }

        // 사용자 인증 정보 확인
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            memberId = Long.parseLong(authentication.getName());
            //followingIds = postService.getFollowingIds(memberId);
        }

        // 게시판 타입 변환
        Post.BoardType boardType = PostConverter.toBoardType(postType);

        // 커서 기반 페이지네이션 조회
        List<Post> posts = postService.findByCursor(boardType, limit, cursor);

        // 회원 정보 및 좋아요 정보 조회
        Map<Long, Map<String, String>> memberInfoMap = postService.getMemberInfoByPosts(posts);
        List<Long> likedPostIds = memberId != null
                ? postLikeService.findLikedPostIdsByMember(memberId, posts)
                : List.of();

        // 게시글별 좋아요한 사용자 목록 조회 (최대 2명)
        //Map<Long, List<String>> whoLikedMap = postService.getWhoLikedPosts(posts, 2);

        // 응답 생성
        PostResponseDto.PostListResponse response = PostConverter.toPostListResponse(
                posts, memberInfoMap, likedPostIds, followingIds, memberId);

        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 정보를 조회합니다.
     */
    @GetMapping("/{postType}/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "게시글의 상세 정보를 조회합니다.")
    @PreAuthorize("@accessChecker.hasAccessToBoard(#postType)")
    public ResponseEntity<PostResponseDto.PostDetailResponse> getPostDetail(
            @Parameter(description = "게시판 유형") @PathVariable String postType,
            @Parameter(description = "게시글 ID") @PathVariable Long postId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = null;
        boolean isFollowing = false;

        // 사용자 인증 정보 확인
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            memberId = Long.parseLong(authentication.getName());
        }

        // 게시글 조회
        Post post = postService.findById(postId);

        // 게시판 타입 검증
        Post.BoardType boardType = PostConverter.toBoardType(postType);
        if (!post.getBoardType().equals(boardType)) {
            throw new PostException(GeneralErrorCode.RESOURCE_NOT_FOUND, "postId", "요청한 게시판에 해당 게시글이 존재하지 않습니다.");
        }

        // 작성자 정보 조회
        Map<String, String> userInfo = postService.getMemberInfo(post.getMemberId());

        // 본인 게시글 여부, 좋아요 여부, 팔로우 여부 확인
        boolean isMine = memberId != null && memberId.equals(post.getMemberId());
        boolean isLiked = memberId != null && postLikeService.isLikedByMember(postId, memberId);

//        // 팔로우 여부 확인
//        if (memberId != null && !isMine) {
//            isFollowing = postService.isFollowing(memberId, post.getMemberId());
//        }

        // 응답 생성
        PostResponseDto.PostDetailResponse response = PostConverter.toPostDetailResponse(
                post, userInfo, isMine, isLiked, isFollowing);

        return ResponseEntity.ok(response);
    }

    /**
     * 새 게시글을 생성합니다.
     */
    @PostMapping("/{postType}")
    @Operation(summary = "게시글 생성", description = "새 게시글을 생성합니다.")
    @PreAuthorize("isAuthenticated() && @accessChecker.hasAccessToBoard(#postType)")
    public ResponseEntity<PostResponseDto.PostCreateResponse> createPost(
            @Parameter(description = "게시판 유형") @PathVariable String postType,
            @Valid @RequestBody PostRequestDto.PostCreateRequestDto requestDto) {

        // SecurityUtil 사용하여 ID 가져오기 - 이 시점에서는 인증된 사용자임이 보장됨
        Long memberId = SecurityUtil.getMemberIdAsLong()
                .orElseThrow(() -> new PostException(GeneralErrorCode.RESOURCE_NOT_FOUND, "memberId", "memberId를 찾을 수 없습니다"));


        // 게시글 내용 유효성 검증
        if (requestDto.isEmpty()) {
            throw new PostException(PostErrorCode.EMPTY_POST_CONTENT);
        }

        // 유튜브 URL 유효성 검증
        if (!requestDto.isValidYoutubeUrl()) {
            throw new PostException(PostErrorCode.INVALID_YOUTUBE_URL);
        }

        // 게시판 타입 변환
        Post.BoardType boardType = PostConverter.toBoardType(postType);

        // 게시글 생성
        Post createdPost = postService.createPost(boardType, requestDto, memberId);

        // 작성자 정보 조회
        Map<String, String> userInfo = postService.getMemberInfo(memberId);

        // 응답 생성
        PostResponseDto.PostCreateResponse response = PostConverter.toPostCreateResponse(
                createdPost, userInfo, false);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 게시글을 삭제합니다.
     */
    @DeleteMapping("/{postType}/{postId}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @PreAuthorize("@accessChecker.hasAccessToBoard(#postType) and @accessChecker.isPostOwner(#postId, authentication)")
    public ResponseEntity<PostResponseDto.PostDeleteResponse> deletePost(
            @Parameter(description = "게시판 유형") @PathVariable String postType,
            @Parameter(description = "게시글 ID") @PathVariable Long postId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = Long.parseLong(authentication.getName());

        // 게시글 삭제
        postService.deletePost(postId, memberId);

        // 응답 생성
        PostResponseDto.PostDeleteResponse response = PostConverter.toPostDeleteResponse();

        return ResponseEntity.ok(response);
    }

    /**
     * 게시글에 좋아요를 추가합니다.
     */
    @PostMapping("/{postId}/likes")
    @Operation(summary = "게시글 좋아요 추가", description = "게시글에 좋아요를 추가합니다.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponseDto.PostLikeResponse> addLike(
            @Parameter(description = "게시글 ID") @PathVariable Long postId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = Long.parseLong(authentication.getName());

        // 좋아요 추가
        postLikeService.addLike(postId, memberId);

        // 응답 생성
        PostResponseDto.PostLikeResponse response = PostConverter.toPostLikeResponse(true);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 게시글 좋아요를 취소합니다.
     */
    @DeleteMapping("/{postId}/likes")
    @Operation(summary = "게시글 좋아요 취소", description = "게시글 좋아요를 취소합니다.")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponseDto.PostLikeResponse> removeLike(
            @Parameter(description = "게시글 ID") @PathVariable Long postId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = Long.parseLong(authentication.getName());

        // 좋아요 취소
        postLikeService.removeLike(postId, memberId);

        // 응답 생성
        PostResponseDto.PostLikeResponse response = PostConverter.toPostLikeResponse(false);

        return ResponseEntity.ok(response);
    }
}