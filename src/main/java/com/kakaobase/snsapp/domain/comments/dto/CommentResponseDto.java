package com.kakaobase.snsapp.domain.comments.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 댓글 관련 응답 DTO 클래스
 * <p>
 * 댓글 생성, 조회, 삭제, 좋아요 등 댓글 관련 API 응답에 사용되는 DTO들을 포함합니다.
 * </p>
 */
@Schema(description = "댓글 관련 응답 DTO 클래스")
public class CommentResponseDto {

    /**
     * 댓글 작성자 정보 DTO
     */
    @Schema(description = "댓글 작성자 정보")
    public record UserInfo(
            @Schema(description = "회원 ID", example = "10")
            Long id,

            @Schema(description = "회원 닉네임", example = "댓글러")
            String nickname,

            @Schema(description = "회원 프로필 이미지 URL", example = "https://cdn.service.com/img1.jpg", nullable = true)
            String profile_image,

            @Schema(description = "팔로우 여부", example = "false")
            boolean is_followed
    ) {}

    /**
     * 대댓글 정보 DTO
     */
    @Schema(description = "대댓글 정보")
    public record RecommentInfo(
            @Schema(description = "대댓글 ID", example = "201")
            Long id,

            @Schema(description = "작성자 정보")
            UserInfo user,

            @Schema(description = "대댓글 내용", example = "저도 그렇게 생각해요!")
            String content,

            @Schema(description = "작성 시간", example = "2024-04-25T13:15:00Z")
            LocalDateTime created_at,

            @Schema(description = "좋아요 수", example = "1")
            int like_count,

            @Schema(description = "본인 작성 여부", example = "false")
            boolean is_mine,

            @Schema(description = "좋아요 여부", example = "true")
            boolean is_liked
    ) {}

    /**
     * 댓글 정보 DTO
     */
    @Schema(description = "댓글 정보")
    public record CommentInfo(
            @Schema(description = "댓글 ID", example = "101")
            Long id,

            @Schema(description = "작성자 정보")
            UserInfo user,

            @Schema(description = "댓글 내용", example = "이 게시글 정말 유익하네요!")
            String content,

            @Schema(description = "작성 시간", example = "2024-04-25T13:00:00Z")
            LocalDateTime created_at,

            @Schema(description = "좋아요 수", example = "3")
            int like_count,

            @Schema(description = "본인 작성 여부", example = "true")
            boolean is_mine,

            @Schema(description = "좋아요 여부", example = "false")
            boolean is_liked,

            @Schema(description = "대댓글 목록", nullable = true)
            List<RecommentInfo> recomments
    ) {}

    /**
     * 댓글 생성 응답 DTO
     */
    @Schema(description = "댓글 생성 응답")
    public record CreateCommentResponse(
            @Schema(description = "댓글 ID", example = "456")
            Long id,

            @Schema(description = "작성자 정보")
            UserInfo user,

            @Schema(description = "댓글 내용", example = "이 댓글은 정말 유익하네요!")
            String content,

            @Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "101", nullable = true)
            Long parent_id
    ) {}

    /**
     * 댓글 목록 응답 DTO
     */
    @Schema(description = "댓글 목록 응답")
    public record CommentListResponse(
            @Schema(description = "댓글 목록")
            List<CommentInfo> comments,

            @Schema(description = "다음 페이지 존재 여부", example = "true")
            boolean has_next,

            @Schema(description = "다음 페이지 커서", example = "102", nullable = true)
            Long next_cursor
    ) {}

    /**
     * 대댓글 목록 응답 DTO
     */
    @Schema(description = "대댓글 목록 응답")
    public record RecommentListResponse(
            @Schema(description = "대댓글 목록")
            List<RecommentInfo> recomments,

            @Schema(description = "다음 페이지 존재 여부", example = "true")
            boolean has_next,

            @Schema(description = "다음 페이지 커서", example = "202", nullable = true)
            Long next_cursor
    ) {}

    /**
     * 댓글 좋아요 토글 응답 DTO
     */
    @Schema(description = "댓글 좋아요 토글 응답")
    public record CommentLikeResponse(
            @Schema(description = "좋아요 상태 (true: 좋아요 등록, false: 좋아요 취소)", example = "true")
            boolean liked,

            @Schema(description = "좋아요 수", example = "4")
            int like_count
    ) {}

    /**
     * 대댓글 좋아요 토글 응답 DTO
     */
    @Schema(description = "대댓글 좋아요 토글 응답")
    public record RecommentLikeResponse(
            @Schema(description = "좋아요 상태 (true: 좋아요 등록, false: 좋아요 취소)", example = "true")
            boolean liked,

            @Schema(description = "좋아요 수", example = "2")
            int like_count
    ) {}

    /**
     * 기본 응답 메시지 DTO
     */
    @Schema(description = "기본 응답 메시지")
    public record MessageResponse(
            @Schema(description = "응답 메시지", example = "댓글이 삭제되었습니다.")
            String message
    ) {}

    /**
     * 에러 응답 DTO
     */
    @Schema(description = "에러 응답")
    public record ErrorResponse(
            @Schema(description = "에러 코드", example = "resource_not_found")
            String error,

            @Schema(description = "에러 메시지", example = "해당 댓글을 찾을 수 없습니다.")
            String message,

            @Schema(description = "에러가 발생한 필드", example = "commentId", nullable = true)
            String field
    ) {}
}