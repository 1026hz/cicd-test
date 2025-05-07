package com.kakaobase.snsapp.global.common.s3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Presigned URL 발급 요청을 위한 DTO 클래스
 *
 * <p>이 클래스는 S3에 이미지를 업로드하기 위한 Presigned URL을 발급받을 때
 * 필요한 정보를 담고 있습니다.</p>
 *
 * <p>API 경로: /images/presigned-url (GET)</p>
 */
@Schema(description = "Presigned URL 발급 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresignedUrlRequestDto {

    /**
     * 원본 파일명
     */
    @Schema(description = "업로드할 파일의 원본 이름", example = "profile.jpg", required = true)
    @NotBlank(message = "파일명은 필수입니다")
    private String fileName;

    /**
     * 파일 크기 (바이트 단위)
     */
    @Schema(description = "파일 크기 (바이트 단위)", example = "1024000", required = true)
    @NotNull(message = "파일 크기는 필수입니다")
    @Positive(message = "파일 크기는 양수여야 합니다")
    private Long fileSize;

    /**
     * 파일의 MIME 타입
     */
    @Schema(description = "파일의 MIME 타입", example = "image/jpeg", required = true)
    @NotBlank(message = "MIME 타입은 필수입니다")
    private String mimeType;

    /**
     * 이미지 타입
     */
    @Schema(description = "이미지 사용 용도 (profile_image, post_image 등)", example = "profile_image", required = true)
    @NotBlank(message = "이미지 타입은 필수입니다")
    private String type;
}