package com.kakaobase.snsapp.domain.posts.converter;

import com.kakaobase.snsapp.domain.posts.dto.PostRequestDto;
import com.kakaobase.snsapp.domain.posts.dto.PostResponseDto;
import com.kakaobase.snsapp.domain.posts.entity.Post;
import com.kakaobase.snsapp.domain.posts.entity.PostImage;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Post 도메인의 Entity와 DTO 간 변환을 담당하는 Converter 클래스
 */
public class PostConverter {

    /**
     * 게시글 생성 요청 DTO를 Post 엔티티로 변환합니다.
     *
     * @param requestDto 게시글 생성 요청 DTO
     * @param memberId 작성자 ID
     * @param boardType 게시판 타입
     * @return 생성된 Post 엔티티
     */
    public static Post toPost(
            PostRequestDto.PostCreateRequestDto requestDto,
            Long memberId,
            Post.BoardType boardType) {

        return Post.builder()
                .memberId(memberId)
                .boardType(boardType)
                .content(requestDto.content())
                .youtubeUrl(requestDto.youtube_url())
                .build();
    }

    /**
     * Post 엔티티를 상세 응답 DTO로 변환합니다.
     *
     * @param post 게시글 엔티티
     * @param userInfo 작성자 정보 (닉네임, 프로필 이미지 등)
     * @param isMine 본인 게시글 여부
     * @param isLiked 좋아요 여부
     * @param isFollowing 작성자 팔로우 여부
     * @return 게시글 상세 응답 DTO
     */
    public static PostResponseDto.PostDetailResponse toPostDetailResponse(
            Post post,
            Map<String, String> userInfo,
            boolean isMine,
            boolean isLiked,
            boolean isFollowing) {

        // 사용자 정보 생성
        PostResponseDto.UserInfo user = new PostResponseDto.UserInfo(
                post.getMemberId(),
                userInfo.get("nickname"),
                userInfo.get("imageUrl"),
                isFollowing
        );

        // 이미지 URL 가져오기 (첫 번째 이미지만 사용)
        String imageUrl = post.getImages().stream()
                .min((i1, i2) -> Integer.compare(i1.getSortIndex(), i2.getSortIndex()))
                .map(PostImage::getImgUrl)
                .orElse(null);

        // 상세 정보 생성
        PostResponseDto.PostDetail data = new PostResponseDto.PostDetail(
                post.getId(),
                user,
                post.getContent(),
                imageUrl,
                post.getYoutubeUrl(),
                post.getYoutubeSummary(),
                post.getCreatedAt(),
                post.getLikeCount(),
                post.getCommentCount(),
                isMine,
                isLiked
        );

        return new PostResponseDto.PostDetailResponse(
                "게시글 상세 정보를 불러왔습니다.",
                data
        );
    }

    /**
     * Post 엔티티 목록을 목록 응답 DTO로 변환합니다.
     *
     * @param posts 게시글 엔티티 목록
     * @param memberInfoMap 회원 정보 맵 (회원 ID -> 맵(닉네임, 프로필 이미지))
     * @param likedPostIds 좋아요한 게시글 ID 목록
     * @param followingIds 팔로우 중인 사용자 ID 목록
     * @param myId 현재 로그인한 사용자 ID
     * @return 게시글 목록 응답 DTO
     */
    public static PostResponseDto.PostListResponse toPostListResponse(
            List<Post> posts,
            Map<Long, Map<String, String>> memberInfoMap,
            List<Long> likedPostIds,
            List<Long> followingIds,
            Long myId //,Map<Long, List<String>> whoLikedMap
            ) {

        List<PostResponseDto.PostListItem> items = posts.stream()
                .map(post -> {
                    Map<String, String> userInfo = memberInfoMap.get(post.getMemberId());
                    if (userInfo == null) {
                        // 서비스 레이어에서 memberInfoMap 구성 시 모든 회원 정보가 포함되어야 함
                        // 여기서 NullPointerException이 발생하면 서비스 레이어의 문제
                        throw new IllegalStateException("사용자 정보를 찾을 수 없습니다: " + post.getMemberId());
                    }

                    PostResponseDto.UserInfo user = new PostResponseDto.UserInfo(
                            post.getMemberId(),
                            userInfo.get("nickname"),
                            userInfo.get("imageUrl"),
                            followingIds.contains(post.getMemberId())
                    );

                    // 이미지 URL 가져오기 (첫 번째 이미지만 사용)
                    String imageUrl = post.getImages().stream()
                            .min((i1, i2) -> Integer.compare(i1.getSortIndex(), i2.getSortIndex()))
                            .map(PostImage::getImgUrl)
                            .orElse(null);

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
                })
                .collect(Collectors.toList());

        return new PostResponseDto.PostListResponse(
                "게시글을 불러오는데 성공하였습니다",
                items
        );
    }

    /**
     * Post 엔티티를 생성 응답 DTO로 변환합니다.
     *
     * @param post 생성된 게시글 엔티티
     * @param userInfo 작성자 정보 (닉네임, 프로필 이미지 등)
     * @param isFollowing 작성자 팔로우 여부
     * @return 게시글 생성 응답 DTO
     */
    public static PostResponseDto.PostCreateResponse toPostCreateResponse(
            Post post,
            Map<String, String> userInfo,
            boolean isFollowing) {

        // 사용자 정보 생성
        PostResponseDto.UserInfo user = new PostResponseDto.UserInfo(
                post.getMemberId(),
                userInfo.get("nickname"),
                userInfo.get("imageUrl"),
                isFollowing
        );

        // 이미지 URL 가져오기 (첫 번째 이미지만 사용)
        String imageUrl = post.getImages().stream()
                .min((i1, i2) -> Integer.compare(i1.getSortIndex(), i2.getSortIndex()))
                .map(PostImage::getImgUrl)
                .orElse(null);

        // 상세 정보 생성
        PostResponseDto.PostDetail data = new PostResponseDto.PostDetail(
                post.getId(),
                user,
                post.getContent(),
                imageUrl,
                post.getYoutubeUrl(),
                post.getYoutubeSummary(),
                post.getCreatedAt(),
                post.getLikeCount(),
                post.getCommentCount(),
                true, // 본인 게시글이므로 true
                false // 생성 시점에는 좋아요를 누르지 않았으므로 false
        );

        return new PostResponseDto.PostCreateResponse(
                "게시글이 작성되었습니다.",
                data
        );
    }

    /**
     * 게시글 삭제 응답 DTO를 생성합니다.
     *
     * @return 게시글 삭제 응답 DTO
     */
    public static PostResponseDto.PostDeleteResponse toPostDeleteResponse() {
        return new PostResponseDto.PostDeleteResponse(
                "게시글이 삭제되었습니다.",
                null
        );
    }

    /**
     * 게시글 좋아요 응답 DTO를 생성합니다.
     *
     * @param isAdd 좋아요 추가 여부 (true: 추가, false: 취소)
     * @return 게시글 좋아요 응답 DTO
     */
    public static PostResponseDto.PostLikeResponse toPostLikeResponse(boolean isAdd) {
        String message = isAdd
                ? "좋아요가 성공적으로 등록되었습니다."
                : "좋아요가 성공적으로 취소되었습니다.";

        return new PostResponseDto.PostLikeResponse(
                message,
                null
        );
    }

    /**
     * 문자열 형태의 postType을 BoardType enum으로 변환합니다.
     *
     * @param postType 게시판 타입 문자열
     * @return BoardType enum 값
     * @throws IllegalArgumentException 유효하지 않은 postType인 경우
     */
    public static Post.BoardType toBoardType(String postType) {
        try {
            if ("all".equalsIgnoreCase(postType)) {
                return Post.BoardType.ALL;
            }

            // snake_case를 대문자와 underscore로 변환 (pangyo_1 -> PANGYO_1)
            String enumFormat = postType.toUpperCase();
            return Post.BoardType.valueOf(enumFormat);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 게시판 타입입니다: " + postType);
        }
    }

    /**
     * BoardType enum을 문자열 형태의 postType으로 변환합니다.
     *
     * @param boardType BoardType enum 값
     * @return 게시판 타입 문자열
     */
    public static String toPostType(Post.BoardType boardType) {
        return boardType.name().toLowerCase();
    }
}