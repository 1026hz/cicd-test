package com.kakaobase.snsapp.global.common.s3.exception;

import com.kakaobase.snsapp.global.error.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * S3 서비스에서 발생할 수 있는 에러 코드를 정의하는 열거형 클래스
 *
 * <p>이 클래스는 S3 서비스와 관련된 모든 에러 코드를 정의합니다.
 * 각 에러 코드는 HTTP 상태 코드, 에러 식별자, 에러 메시지, 해당 필드를 포함합니다.</p>
 */
@Getter
@AllArgsConstructor
public enum S3ErrorCode implements BaseErrorCode {

    // 400 Bad Request
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "file_size_exceeded", "파일 용량은 10MB를 초과할 수 없습니다.", "fileSize"),
    UNSUPPORTED_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "unsupported_image_format", "지원하지 않는 이미지 형식입니다.", "mimeType"),
    INVALID_IMAGE_URL(HttpStatus.BAD_REQUEST, "invalid_image_url", "유효하지 않은 이미지 주소입니다.", "imageUrl"),


    // 403 Forbidden
    S3_ACCESS_DENIED(HttpStatus.FORBIDDEN, "s3_access_denied", "S3 리소스에 대한 접근이 거부되었습니다.", null);


    private final HttpStatus status;
    private final String error;
    private final String message;
    private final String field;
}