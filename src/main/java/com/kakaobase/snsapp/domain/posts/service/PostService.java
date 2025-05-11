package com.kakaobase.snsapp.domain.posts.service;

import com.kakaobase.snsapp.domain.members.service.MemberService;
import com.kakaobase.snsapp.domain.posts.converter.PostConverter;
import com.kakaobase.snsapp.domain.posts.dto.PostRequestDto;
import com.kakaobase.snsapp.domain.posts.dto.PostResponseDto;
import com.kakaobase.snsapp.domain.posts.entity.Post;
import com.kakaobase.snsapp.domain.posts.entity.PostImage;
import com.kakaobase.snsapp.domain.posts.event.PostCreatedEvent;
import com.kakaobase.snsapp.domain.posts.exception.PostErrorCode;
import com.kakaobase.snsapp.domain.posts.exception.PostException;
import com.kakaobase.snsapp.domain.posts.repository.PostImageRepository;
import com.kakaobase.snsapp.domain.posts.repository.PostRepository;
import com.kakaobase.snsapp.global.common.s3.service.S3Service;
import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import org.springframework.context.ApplicationEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 게시글 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final S3Service s3Service;
    private final MemberService memberService;
    private final YouTubeSummaryService youtubeSummaryService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PostLikeService postLikeService;

    /**
     * 게시글을 생성합니다.
     *
     * @param boardType 게시판 유형
     * @param requestDto 게시글 생성 요청 DTO
     * @param memberId 작성자 ID
     * @return 생성된 게시글 엔티티
     */
    @Transactional
    public Post createPost(Post.BoardType boardType, PostRequestDto.PostCreateRequestDto requestDto, Long memberId) {
        // 이미지 URL 유효성 검증
        if (StringUtils.hasText(requestDto.image_url()) && !s3Service.isValidImageUrl(requestDto.image_url())) {
            throw new PostException(PostErrorCode.INVALID_IMAGE_URL);
        }

        // 게시글 엔티티 생성
        Post post = PostConverter.toPost(requestDto, memberId, boardType);

        // 게시글 저장
        Post savedPost = postRepository.save(post);

        // 게시글 이미지 저장
        if(requestDto.image_url() != null) {
            PostImage postImage = PostConverter.toPostImage(post, 0, requestDto.image_url());
            postImageRepository.save(postImage);
        }

        // 게시글 생성 이벤트 발행
        applicationEventPublisher.publishEvent(new PostCreatedEvent(savedPost.getId(), boardType, memberId));

        log.info("게시글 생성 완료: 게시글 ID={}, 작성자 ID={}, 게시판={}", savedPost.getId(), memberId, boardType);
        return savedPost;
    }

    /**
     * 게시글 상세 정보를 조회합니다.
     *
     * @param postId 게시글 ID
     * @param memberId 현재 사용자 ID
     * @return 게시글 상세 정보
     */
    public PostResponseDto.PostDetailResponse getPostDetail(Long postId, Long memberId) {
        // 게시글 조회
        Post post = findById(postId);

        // 작성자 정보 조회
        Map<String, String> userInfo = getMemberInfo(post.getMemberId());

        // 본인 게시글 여부 확인
        boolean isMine = memberId != null && memberId.equals(post.getMemberId());

        // 좋아요 여부 확인
        boolean isLiked = memberId != null && postLikeService.isLikedByMember(postId, memberId);

        // 팔로우 여부는 현재 비활성화 되어있으므로 false로 설정
        boolean isFollowing = false;

        // 이미지 조회
        List<PostImage> postImages = postImageRepository.findByPostIdOrderBySortIndexAsc(post.getId());

        // 응답 DTO 생성 및 반환
        return PostConverter.toPostDetailResponse(
                post, userInfo, postImages, isMine, isLiked, isFollowing);
    }

    /**
     * 커서 기반 페이지네이션으로 게시글을 조회합니다.
     *
     * @param boardType 게시판 유형
     * @param limit 한 페이지에 표시할 게시글 수
     * @param cursor 마지막으로 조회한 게시글 ID
     * @return 게시글 목록
     */
    public List<Post> findByCursor(Post.BoardType boardType, int limit, Long cursor) {
        if (cursor == null) {
            // 첫 페이지 조회
            return postRepository.findTopNByBoardTypeOrderByCreatedAtDescIdDesc(boardType, limit);
        } else {
            // 다음 페이지 조회 (cursor 이후 데이터)
            return postRepository.findByBoardTypeAndIdLessThanOrderByIdDesc(boardType, cursor, limit);
        }
    }

    /**
     * 게시글 ID로 게시글을 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 조회된 게시글 엔티티
     */
    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostException(GeneralErrorCode.RESOURCE_NOT_FOUND, "postId", "해당 게시글을 찾을 수 없습니다"));
    }

    /**
     * 게시글을 삭제합니다.
     *
     * @param postId 게시글 ID
     * @param memberId 삭제자 ID
     */
    @Transactional
    public void deletePost(Long postId, Long memberId) {
        // 게시글 조회 - AccessChecker에서 이미 권한 검증을 했으므로 간소화 가능
        Post post = findById(postId);

        // 소프트 삭제 처리
        postRepository.delete(post);

        log.info("게시글 삭제 완료: 게시글 ID={}, 삭제자 ID={}", postId, memberId);
    }

    /**
     * 게시글 목록을 조회합니다.
     *
     * @param postType 게시판 유형
     * @param limit 페이지 크기
     * @param cursor 커서
     * @param memberId 현재 사용자 ID (nullable)
     * @return 게시글 목록 응답
     */
    /**
     * 게시글 목록을 조회합니다.
     */
    public PostResponseDto.PostListResponse getPostList(String postType, int limit, Long cursor, Long currentMemberId) {
        // 1. 유효성 검증
        if (limit < 1) {
            throw new PostException(GeneralErrorCode.INVALID_QUERY_PARAMETER, "limit", "limit는 1 이상이어야 합니다.");
        }

        // 2. 게시판 타입 변환
        Post.BoardType boardType = PostConverter.toBoardType(postType);

        // 3. 게시글 조회
        List<Post> posts = findByCursor(boardType, limit, cursor);

        // 4. 작성자 정보 조회
        Map<Long, Map<String, String>> memberInfoMap = getMemberInfoByPosts(posts);

        // 5. 좋아요 정보 조회
        List<Long> likedPostIds = currentMemberId != null
                ? postLikeService.findLikedPostIdsByMember(currentMemberId, posts)
                : List.of();

        // 6. 팔로우 정보 조회 (현재는 미구현)
        List<Long> followingIds = List.of();

        // 7. 게시글별 좋아요한 사용자 목록 조회 (현재는 미구현)
        Map<Long, List<String>> whoLikedMap = Map.of();

        // 8. 각 게시글의 첫 번째 이미지 URL 조회
        Map<Long, String> firstImageUrlMap = findFirstImageUrlsByPosts(posts);

        // 9. Entity → DTO 변환
        List<PostResponseDto.PostListItem> items = posts.stream()
                .map(post -> PostConverter.toPostListItem(
                        post,
                        memberInfoMap.get(post.getMemberId()),
                        firstImageUrlMap.get(post.getId()), // 이미지 URL 전달
                        likedPostIds.contains(post.getId()),
                        followingIds.contains(post.getMemberId()),
                        currentMemberId != null && currentMemberId.equals(post.getMemberId())
                ))
                .collect(Collectors.toList());

        // 10. 최종 응답 DTO 생성
        return PostConverter.toPostListResponse(items, "게시글을 불러오는데 성공하였습니다");
    }

    /**
     * 개별 게시글을 PostListItem DTO로 변환합니다.
     */
    private PostResponseDto.PostListItem createPostListItem(
            Post post,
            Map<Long, Map<String, String>> memberInfoMap,
            List<Long> likedPostIds,
            List<Long> followingIds,
            Long myId) {

        // 회원 정보 조회
        Map<String, String> userInfo = memberInfoMap.get(post.getMemberId());

        // UserInfo DTO 생성
        PostResponseDto.UserInfo user = new PostResponseDto.UserInfo(
                post.getMemberId(),
                userInfo.get("nickname"),
                userInfo.get("imageUrl"),
                followingIds.contains(post.getMemberId())
        );

        // 첫 번째 이미지 URL 조회
        String imageUrl = postImageRepository.findByPostIdOrderBySortIndexAsc(post.getId()).getFirst().getImgUrl().toString();

        // 본인 게시글 여부 및 좋아요 여부 확인
        boolean isMine = myId != null && myId.equals(post.getMemberId());
        boolean isLiked = likedPostIds.contains(post.getId());

        return new PostResponseDto.PostListItem(
                post.getId(),
                user,
                post.getContent(),
                imageUrl,
                post.getYoutubeUrl(),
                post.getCreatedAt(),
                post.getLikeCount(),
                post.getCommentCount(),
                isMine,
                isLiked
        );
    }

    /**
     * 회원 ID로 회원 정보를 조회합니다.
     *
     * @param memberId 회원 ID
     * @return 회원 정보 (닉네임, 프로필 이미지)
     */
    public Map<String, String> getMemberInfo(Long memberId) {
        return memberService.getMemberInfo(memberId);
    }

    /**
     * 게시글 목록에 포함된 회원 정보를 조회합니다.
     *
     * @param posts 게시글 목록
     * @return 회원 ID를 키로 하고 회원 정보(닉네임, 프로필 이미지)를 값으로 하는 맵
     */
    public Map<Long, Map<String, String>> getMemberInfoByPosts(List<Post> posts) {
        // 게시글에 포함된 회원 ID 목록 추출
        List<Long> memberIds = posts.stream()
                .map(Post::getMemberId)
                .distinct()
                .collect(Collectors.toList());

        if (memberIds.isEmpty()) {
            return Map.of();
        }

        // MemberService를 통해 회원 정보 조회
        return memberService.getMemberInfoMapByIds(memberIds);
    }
    /**
     * 게시글 목록의 첫 번째 이미지 URL을 조회합니다.
     *
     * @param posts 게시글 목록
     * @return 게시글 ID를 키로, 첫 번째 이미지 URL을 값으로 하는 Map
     */
    public Map<Long, String> findFirstImageUrlsByPosts(List<Post> posts) {
        if (posts.isEmpty()) {
            return Map.of();
        }

        List<Long> postIds = posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());

        // 각 게시글의 첫 번째 이미지만 조회 (sortIndex가 가장 작은 것)
        List<PostImage> firstImages = postImageRepository.findFirstImagesByPostIds(postIds);

        // postId를 키로, imgUrl을 값으로 하는 Map 생성
        return firstImages.stream()
                .collect(Collectors.toMap(
                        postImage -> postImage.getPost().getId(),
                        PostImage::getImgUrl
                ));
    }


    /**
     * YouTube 영상 요약
     *
     * <p>게시글에 포함된 YouTube URL의 영상을 요약하고 결과를 저장합니다.</p>
     *
     * @param postId 요약할 게시글의 ID
     * @param memberId 요청한 사용자의 ID
     * @return YouTube 요약 응답 DTO
     * @throws PostException 게시글을 찾을 수 없거나, 권한이 없거나, YouTube URL이 없는 경우
     */
    @Transactional
    public PostResponseDto.YouTubeSummaryResponse summarizeYoutube(Long postId, Long memberId) {
        log.info("YouTube 요약 요청 - postId: {}, memberId: {}", postId, memberId);

        // 1. 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("게시글을 찾을 수 없음 - postId: {}", postId);
                    return new PostException(GeneralErrorCode.RESOURCE_NOT_FOUND, "postId");
                });


        // 3. YouTube URL 존재 확인
        String youtubeUrl = post.getYoutubeUrl();
        if (youtubeUrl == null || youtubeUrl.isBlank()) {
            log.error("YouTube URL이 없음 - postId: {}", postId);
            throw new PostException(GeneralErrorCode.INVALID_FORMAT, "youtubeUrl");
        }

        // 4. AI 서버에 요약 요청
        log.info("AI 서버에 YouTube 요약 요청 - postId: {}, url: {}", postId, youtubeUrl);
        String summary = youtubeSummaryService.getSummary(youtubeUrl);

        // 5. 요약 내용 저장
        post.setYoutubeSummary(summary);
        log.info("YouTube 요약 저장 완료 - postId: {}", postId);

        // 6. 응답 생성
        return PostResponseDto.YouTubeSummaryResponse.of(summary);
    }

//    /**
//     * 게시글에 좋아요한 사용자 목록을 조회합니다.
//     *
//     * @param posts 게시글 목록
//     * @param limit 조회할 사용자 수 제한
//     * @return 게시글 ID를 키로 하고 좋아요한 사용자 닉네임 목록을 값으로 하는 맵
//     */
//    public Map<Long, List<String>> getWhoLikedPosts(List<Post> posts, int limit) {
//        Map<Long, List<String>> result = new HashMap<>();
//
//        for (Post post : posts) {
//            List<String> whoLiked = postLikeService.findWhoLikedPost(post.getId(), limit);
//            result.put(post.getId(), whoLiked);
//        }
//
//        return result;
//    }

//    /**
//     * 사용자가 팔로우하는 회원 ID 목록을 조회합니다.
//     *
//     * @param memberId 사용자 ID
//     * @return 팔로우하는 회원 ID 목록
//     */
//    public List<Long> getFollowingIds(Long memberId) {
//        return followService.getFollowingIds(memberId);
//    }
//
//    /**
//     * 사용자가 특정 회원을 팔로우하는지 확인합니다.
//     *
//     * @param memberId 사용자 ID
//     * @param targetId 대상 회원 ID
//     * @return 팔로우 여부
//     */
//    public boolean isFollowing(Long memberId, Long targetId) {
//        return followService.isFollowing(memberId, targetId);
//    }
//
//    /**
//     * 유튜브 요약 내용을 업데이트합니다.`
//     *
//     * @param postId 게시글 ID
//     * @param summary 요약 내용
//     */
//    @Transactional
//    public void updateYoutubeSummary(Long postId, String summary) {
//        Post post = findById(postId);
//        post.updateYoutubeSummary(summary);
//        log.info("유튜브 요약 업데이트 완료: 게시글 ID={}", postId);
//    }
}