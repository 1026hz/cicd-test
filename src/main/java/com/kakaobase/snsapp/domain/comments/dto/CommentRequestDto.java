package com.kakaobase.snsapp.domain.comments.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 댓글 관련 요청 DTO 클래스
 * <p>
 * 댓글 생성, 수정, 조회, 좋아요 등 댓글 관련 API 요청에 사용되는 DTO들을 포함합니다.
 * </p>
 */
@Schema(description = "댓글 관련 요청 DTO 클래스")
public class CommentRequestDto {

    /**
     * 댓글 작성 요청 DTO
     */
    @Schema(description = "댓글 작성 요청 DTO")
    public record CreateCommentRequest(
            @Schema(description = "댓글 내용", example = "이 댓글은 정말 유익하네요!", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "댓글 내용은 공백일 수 없습니다.")
            @Size(min = 1, max = 2000, message = "댓글은 최대 2000자까지 작성할 수 있습니다.")
            String content,

            @Schema(description = "대댓글일 경우 부모 댓글 ID, 없으면 일반 댓글", example = "101", nullable = true)
            Long parent_id
    ) {}


    /**
     * 댓글 목록 조회 요청 DTO
     */
    @Schema(description = "댓글 목록 조회 요청 DTO")
    public record CommentPageRequest(
            @Schema(description = "한 번에 불러올 댓글 수", example = "12", defaultValue = "12")
            Integer limit,

            @Schema(description = "페이지네이션 기준 커서 (이전 응답의 마지막 댓글 ID)", example = "123", nullable = true)
            Long cursor
    ) {
        public CommentPageRequest {
            if (limit == null) {
                limit = 12;
            }
        }
    }

    /**
     * 대댓글 목록 조회 요청 DTO
     */
    @Schema(description = "대댓글 목록 조회 요청 DTO")
    public record RecommentPageRequest(
            @Schema(description = "한 번에 불러올 대댓글 수", example = "12", defaultValue = "12")
            Integer limit,

            @Schema(description = "페이지네이션 기준 커서 (이전 응답의 마지막 대댓글 ID)", example = "123", nullable = true)
            Long cursor
    ) {
        public RecommentPageRequest {
            if (limit == null) {
                limit = 12;
            }
        }
    }

    /**
     * 댓글 신고 요청 DTO (V2에서 구현 예정)
     */
    @Schema(description = "댓글 신고 요청 DTO")
    public record CommentReportRequest(
            @Schema(description = "신고할 댓글 ID", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull(message = "댓글 ID는 필수입니다.")
            Long commentId,

            @Schema(description = "신고 사유", example = "스팸 또는 광고 내용", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "신고 사유는 필수입니다.")
            String reason
    ) {}

    /**
     * 대댓글 신고 요청 DTO (V2에서 구현 예정)
     */
    @Schema(description = "대댓글 신고 요청 DTO")
    public record RecommentReportRequest(
            @Schema(description = "신고할 대댓글 ID", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotNull(message = "대댓글 ID는 필수입니다.")
            Long recommentId,

            @Schema(description = "신고 사유", example = "스팸 또는 광고 내용", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "신고 사유는 필수입니다.")
            String reason
    ) {}
}