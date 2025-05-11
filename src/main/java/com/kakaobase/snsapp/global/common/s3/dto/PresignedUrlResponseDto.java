package com.kakaobase.snsapp.global.common.s3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Presigned URL 발급 응답을 위한 DTO 클래스
 *
 * <p>이 클래스는 S3에 이미지를 업로드하기 위한 Presigned URL 발급 결과를
 * 클라이언트에 전달하기 위한 DTO입니다.</p>
 *
 * <p>API 경로: /images/presigned-url (GET)의 응답 형식</p>
 */
@Schema(description = "Presigned URL 발급 응답 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresignedUrlResponseDto {

    /**
     * Presigned URL
     */
    @Schema(description = "S3에 파일을 업로드하기 위한 Presigned URL",
            example = "https://my-bucket.s3.amazonaws.com/uploads/cat.jpg?AWSAccessKeyId=...",
            required = true)
    private String presinged_url;

    /**
     * 이미지 접근 URL
     */
    @Schema(description = "업로드 완료 후 이미지에 접근할 수 있는 URL",
            example = "https://my-bucket.s3.amazonaws.com/uploads/cat.jpg",
            required = true)
    private String image_url;

    /**
     * URL 만료 시간 (초)
     */
    @Schema(description = "Presigned URL의 유효 시간 (초)",
            example = "300",
            required = true)
    private Integer expires_in;
}