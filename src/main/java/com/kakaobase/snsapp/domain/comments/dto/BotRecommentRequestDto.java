package com.kakaobase.snsapp.domain.comments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * AI 봇 대댓글 관련 요청/응답 DTO 모음
 *
 * <p>AI 서버와의 대댓글 생성 통신에 사용되는 DTO record들을 포함합니다.</p>
 */
@Schema(description = "봇 대댓글 관련 요청/응답 DTO 클래스")
public class BotRecommentRequestDto {

    /**
     * 사용자 정보 DTO (API 스펙에 맞춤)
     * 게시글 작성자, 댓글 작성자, 대댓글 작성자 모두에 사용
     *
     * @param nickname 사용자 닉네임
     * @param className 사용자 기수
     */
    @Schema(description = "사용자 정보")
    public record UserInfo(
            @Schema(description = "사용자 닉네임", example = "roro.bot", required = true)
            String nickname,

            @Schema(description = "사용자 기수", example = "PANGYO_2", required = true)
            @JsonProperty("class_name")
            String className
    ) {}

    /**
     * AI 서버에 대댓글 생성 요청 DTO
     *
     * @param boardType 게시판 타입 (PANGYO_2 등)
     * @param post 원본 게시글 정보
     * @param comment 댓글 정보
     */
    @Schema(description = "AI 서버 대댓글 생성 요청")
    public record CreateRecommentRequest(
            @Schema(description = "게시판 타입", example = "PANGYO_2", required = true)
            @JsonProperty("board_type")
            String boardType,

            @Schema(description = "원본 게시글 정보", required = true)
            BotPost post,

            @Schema(description = "댓글 정보", required = true)
            BotComment comment
    ) {
        /**
         * 유효성 검사를 포함한 생성자
         */
        public CreateRecommentRequest {
            if (boardType == null || boardType.trim().isEmpty()) {
                throw new IllegalArgumentException("게시판 타입은 필수입니다.");
            }
            if (post == null) {
                throw new IllegalArgumentException("게시글 정보는 필수입니다.");
            }
            if (comment == null) {
                throw new IllegalArgumentException("댓글 정보는 필수입니다.");
            }
        }
    }

    /**
     * 게시글 정보 DTO
     *
     * @param id 게시글 ID
     * @param user 작성자 정보
     * @param createdAt 작성 시각 (ISO 8601 형식)
     * @param content 게시글 내용
     */
    @Schema(description = "게시글 정보")
    public record BotPost(
            @Schema(description = "게시글 ID", example = "4040", required = true)
            Long id,

            @Schema(description = "작성자 정보", required = true)
            UserInfo user,

            @Schema(description = "작성 시각", example = "2025-04-27T11:41:32.311141Z", required = true)
            @JsonProperty("created_at")
            String createdAt,

            @Schema(description = "게시글 내용", example = "이번에 해태제과에서 후렌치파이...", required = true)
            String content
    ) {}

    /**
     * 댓글 정보 DTO
     *
     * @param id 댓글 ID
     * @param user 작성자 정보
     * @param createdAt 작성 시각 (ISO 8601 형식)
     * @param content 댓글 내용
     * @param recomments 기존 대댓글 목록
     */
    @Schema(description = "댓글 정보")
    public record BotComment(
            @Schema(description = "댓글 ID", example = "1234", required = true)
            Long id,

            @Schema(description = "작성자 정보", required = true)
            UserInfo user,

            @Schema(description = "작성 시각", example = "2025-04-27T13:40:32.311141Z", required = true)
            @JsonProperty("created_at")
            String createdAt,

            @Schema(description = "댓글 내용", example = "블루베리맛 후렌치파이? 맛있겠다!", required = true)
            String content,

            @Schema(description = "기존 대댓글 목록", nullable = true)
            List<BotRecomment> recomments
    ) {}

    /**
     * 대댓글 정보 DTO
     *
     * @param user 작성자 정보
     * @param createdAt 작성 시각 (ISO 8601 형식)
     * @param content 대댓글 내용
     */
    @Schema(description = "대댓글 정보")
    public record BotRecomment(
            @Schema(description = "작성자 정보", required = true)
            UserInfo user,

            @Schema(description = "작성 시각", example = "2025-04-27T13:41:32.311141Z", required = true)
            @JsonProperty("created_at")
            String createdAt,

            @Schema(description = "대댓글 내용", example = "네 맛있더라구요!><", required = true)
            String content
    ) {}


    /**
     * AI 서버로부터의 대댓글 생성 응답 DTO
     *
     * @param message 응답 메시지
     * @param data 응답 데이터
     */
    @Schema(description = "AI 서버로부터의 대댓글 생성 응답")
    public record AiRecommentResponse(
            @Schema(description = "응답 메시지", example = "소셜봇이 대댓글을 작성했습니다.")
            String message,

            @Schema(description = "응답 데이터")
            AiResponseData data
    ) {}

    /**
     * AI 서버 응답 데이터 DTO
     *
     * @param boardType 게시판 타입
     * @param postId 게시글 ID
     * @param commentId 댓글 ID
     * @param user 봇 사용자 정보
     * @param content 생성된 대댓글 내용
     */
    @Schema(description = "AI 서버 응답 데이터")
    public record AiResponseData(
            @Schema(description = "게시판 타입", example = "PANGYO_2")
            @JsonProperty("board_type")
            String boardType,

            @Schema(description = "게시글 ID", example = "4040")
            @JsonProperty("post_id")
            Long postId,

            @Schema(description = "댓글 ID", example = "1234")
            @JsonProperty("comment_id")
            Long commentId,

            @Schema(description = "봇 사용자 정보")
            UserInfo user,

            @Schema(description = "생성된 대댓글 내용", example = "ㅎㅎ 맞아요! 저는 입은 없지만...")
            String content
    ) {}

    /**
     * AI 서버 에러 응답 DTO
     *
     * @param error 에러 코드
     * @param message 에러 메시지
     * @param field 에러 필드 (optional)
     */
    @Schema(description = "AI 서버 에러 응답")
    public record ErrorResponse(
            @Schema(description = "에러 코드", example = "missing_required_field")
            String error,

            @Schema(description = "에러 메시지", example = "필수 필드가 누락되었습니다.")
            String message,

            @Schema(description = "에러 필드 목록", example = "[\"body\", \"board_type\"]", required = false)
            List<String> field
    ) {}
}