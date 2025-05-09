package com.kakaobase.snsapp.domain.posts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시글 도메인의 응답 DTO를 관리하는 통합 클래스
 */
public class PostResponseDto {

    /**
     * 게시글 상세 정보 응답 DTO
     */
    @Schema(description = "게시글 상세 정보 응답")
    public record PostDetailResponse(
            @Schema(description = "응답 메시지", example = "게시글 상세 정보를 불러왔습니다.")
            String message,

            @Schema(description = "응답 데이터")
            PostDetail data
    ) {}

    @Schema(description = "게시글 상세 정보")
    public record PostDetail(
            @Schema(description = "게시글 ID", example = "123")
            Long id,

            @Schema(description = "작성자 정보")
            UserInfo user,

            @Schema(description = "게시글 내용", example = "오늘도 Typescript 공부 중입니다.")
            String content,

            @Schema(description = "이미지 URL", example = "https://s3.../uploads/dev.jpg")
            @JsonProperty("image_url")
            String imageUrl,

            @Schema(description = "유튜브 URL", example = "https://www.youtube.com/watch?v=abcd1234")
            @JsonProperty("youtube_url")
            String youtubeUrl,

            @Schema(description = "유튜브 요약", example = "이 영상은 타입스크립트의 제네릭에 대해 설명합니다.")
            @JsonProperty("youtube_summary")
            String youtubeSummary,

            @Schema(description = "생성 시간", example = "2024-04-23T12:34:56Z")
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
            @JsonProperty("created_at")
            LocalDateTime createdAt,

            @Schema(description = "좋아요 수", example = "5")
            @JsonProperty("like_count")
            Integer likeCount,

            @Schema(description = "댓글 수", example = "12")
            @JsonProperty("comment_count")
            Integer commentCount,

            @Schema(description = "본인 게시글 여부", example = "true")
            @JsonProperty("is_mine")
            Boolean isMine,

            @Schema(description = "좋아요 여부", example = "true")
            @JsonProperty("is_liked")
            Boolean isLiked
    ) {}

    /**
     * 게시글 목록 조회 응답 DTO
     */
    @Schema(description = "게시글 목록 조회 응답")
    public record PostListResponse(
            @Schema(description = "응답 메시지", example = "게시글을 불러오는데 성공하였습니다")
            String message,

            @Schema(description = "게시글 목록 데이터")
            List<PostListItem> data
    ) {}

    @Schema(description = "게시글 목록 아이템")
    public record PostListItem(
            @Schema(description = "게시글 ID", example = "123")
            Long id,

            @Schema(description = "작성자 정보")
            UserInfo user,

            @Schema(description = "게시글 내용", example = "이벤트 버블링 헷갈릴 때는...")
            String content,

            @Schema(description = "이미지 URL", example = "https://s3.../event-tip.png")
            @JsonProperty("image_url")
            String imageUrl,

            @Schema(description = "유튜브 URL", example = "https://www.youtube.com/watch?v=abcd1234")
            @JsonProperty("youtube_url")
            String youtubeUrl,

            @Schema(description = "생성 시간", example = "2024-04-23T10:00:00Z")
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
            @JsonProperty("created_at")
            LocalDateTime createdAt,

            @Schema(description = "좋아요 수", example = "5")
            @JsonProperty("like_count")
            Integer likeCount,

            @Schema(description = "댓글 수", example = "2")
            @JsonProperty("comment_count")
            Integer commentCount,

            @Schema(description = "본인 게시글 여부", example = "true")
            @JsonProperty("is_mine")
            Boolean isMine,

            @Schema(description = "좋아요 여부", example = "false")
            @JsonProperty("is_liked")
            Boolean isLiked

//            @Schema(description = "좋아요 누른 사용자 목록", example = "[\"backend.sam\", \"choi.dan\"]")
//            @JsonProperty("who_liked")
//            List<String> whoLiked
    ) {}

    /**
     * 사용자 정보 DTO
     */
    @Schema(description = "사용자 정보")
    public record UserInfo(
            @Schema(description = "사용자 ID", example = "7")
            Long id,

            @Schema(description = "닉네임", example = "frontend.kim")
            String nickname,

            @Schema(description = "프로필 이미지 URL", example = "https://s3.../avatar.png")
            @JsonProperty("image_url")
            String imageUrl,

            @Schema(description = "팔로우 여부", example = "true")
            @JsonProperty("is_following")
            Boolean isFollowing
    ) {}

    /**
     * 게시글 생성 응답 DTO
     */
    @Schema(description = "게시글 생성 응답")
    public record PostCreateResponse(
            @Schema(description = "응답 메시지", example = "게시글이 작성되었습니다.")
            String message,

            @Schema(description = "생성된 게시글 정보")
            PostDetail data
    ) {}

    /**
     * 게시글 삭제 응답 DTO
     */
    @Schema(description = "게시글 삭제 응답")
    public record PostDeleteResponse(
            @Schema(description = "응답 메시지", example = "게시글이 삭제되었습니다.")
            String message,

            @Schema(description = "데이터", example = "null")
            Object data
    ) {}

    /**
     * 게시글 좋아요 응답 DTO
     */
    @Schema(description = "게시글 좋아요 응답")
    public record PostLikeResponse(
            @Schema(description = "응답 메시지", example = "좋아요가 성공적으로 등록되었습니다.")
            String message,

            @Schema(description = "데이터", example = "null")
            Object data
    ) {}
}