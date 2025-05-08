package com.kakaobase.snsapp.domain.posts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 게시글 도메인의 요청 DTO를 관리하는 통합 클래스
 */
public class PostRequestDto {

    /**
     * 게시글 생성 요청 DTO
     */
    @Schema(description = "게시글 생성 요청")
    public record PostCreateRequestDto(
            @Schema(description = "게시글 본문", example = "오늘도 열심히 개발 중입니다!", maxLength = 2000)
            @Size(max = 2000, message = "게시글 본문은 최대 2000자까지 작성할 수 있습니다.")
            String content,

            @Schema(description = "이미지 URL", example = "https://s3.amazonaws.com/bucket/uploads/dev.jpg", required = false)
            String image_url,

            @Schema(description = "유튜브 URL", example = "https://www.youtube.com/watch?v=abcd1234", required = false)
            String youtube_url
    ) {
        /**
         * 게시글 내용이 비어있는지 검증합니다.
         * 내용, 이미지, 유튜브 링크 중 하나는 반드시 존재해야 합니다.
         *
         * @return 내용이 비어있으면 true, 아니면 false
         */
        public boolean isEmpty() {
            return (content == null || content.trim().isEmpty())
                    && (image_url == null || image_url.trim().isEmpty())
                    && (youtube_url == null || youtube_url.trim().isEmpty());
        }

        /**
         * 유튜브 URL이 유효한지 검증합니다.
         *
         * @return 유효하면 true, 아니면 false
         */
        public boolean isValidYoutubeUrl() {
            if (youtube_url == null || youtube_url.trim().isEmpty()) {
                return true; // 빈 값은 유효하다고 처리
            }

            // 유튜브 URL 형식 검증 (youtube.com 또는 youtu.be 도메인 확인)
            return youtube_url.contains("youtube.com") || youtube_url.contains("youtu.be");
        }
    }

    /**
     * 게시글 검색 요청 DTO
     */
    @Schema(description = "게시글 검색 요청")
    public record PostSearchRequestDto(
            @Schema(description = "한 페이지에 표시할 게시글 수", example = "12", defaultValue = "12")
            Integer limit,

            @Schema(description = "마지막으로 조회한 게시글 ID (다음 페이지 요청 시)", example = "123")
            Long cursor,

            @Schema(description = "마지막으로 조회한 게시글 생성일시 (다음 페이지 요청 시)", example = "2024-04-23T10:00:00Z")
            String created_at
    ) {}

    /**
     * 이미지 URL 요청 DTO (S3 Presigned URL 발급 요청 시 사용)
     */
    @Schema(description = "이미지 업로드 URL 요청")
    public record ImageUrlRequestDto(
            @Schema(description = "파일명", example = "profile.jpg", required = true)
            @NotBlank(message = "파일명은 필수입니다")
            String filename,

            @Schema(description = "파일 크기(바이트)", example = "1024000", required = true)
            Long size,

            @Schema(description = "파일 MIME 타입", example = "image/jpeg", required = true)
            @NotBlank(message = "MIME 타입은 필수입니다")
            String mime_type
    ) {}
}