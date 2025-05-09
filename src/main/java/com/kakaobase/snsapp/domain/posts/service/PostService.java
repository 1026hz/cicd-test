package com.kakaobase.snsapp.domain.posts.service;

import com.kakaobase.snsapp.domain.members.service.MemberService;
import com.kakaobase.snsapp.domain.posts.dto.PostRequestDto;
import com.kakaobase.snsapp.domain.posts.entity.Post;
import com.kakaobase.snsapp.domain.posts.entity.PostImage;
import com.kakaobase.snsapp.domain.posts.exception.PostErrorCode;
import com.kakaobase.snsapp.domain.posts.exception.PostException;
import com.kakaobase.snsapp.domain.posts.repository.PostRepository;
import com.kakaobase.snsapp.global.common.s3.service.S3Service;
import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
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
    private final S3Service s3Service;
    private final MemberService memberService;
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
        Post post = new Post(
                memberId,
                boardType,
                requestDto.content(),
                requestDto.youtube_url()
        );

        // 게시글 저장
        Post savedPost = postRepository.save(post);

        // 이미지 처리 (단일 이미지)
        if (StringUtils.hasText(requestDto.image_url())) {
            PostImage postImage = new PostImage(savedPost, 0, requestDto.image_url());
            savedPost.addImage(postImage);
        }

        log.info("게시글 생성 완료: 게시글 ID={}, 작성자 ID={}, 게시판={}", savedPost.getId(), memberId, boardType);
        return savedPost;
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