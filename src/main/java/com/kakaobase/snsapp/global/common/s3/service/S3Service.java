package com.kakaobase.snsapp.global.common.s3.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.kakaobase.snsapp.domain.posts.entity.Post;
import com.kakaobase.snsapp.global.common.s3.dto.PresignedUrlResponseDto;
import com.kakaobase.snsapp.global.common.s3.exception.S3ErrorCode;
import com.kakaobase.snsapp.global.common.s3.exception.S3Exception;
import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * S3 관련 기능을 제공하는 서비스 클래스
 *
 * <p>이 클래스는 Amazon S3와의 상호작용을 담당하며, Presigned URL 생성,
 * 이미지 URL 검증, S3 객체 삭제 등의 기능을 제공합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    /**
     * S3 버킷 이름
     */
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * Presigned URL 만료 시간 (초)
     */
    @Value("${cloud.aws.s3.expiration-time:300}")  // 기본값 5분
    private int expirationTime;

    /**
     * 최대 파일 크기 (바이트)
     */
    @Value("${cloud.aws.s3.max-file-size:10485760}")  // 기본값 10MB
    private long maxFileSize;

    /**
     * 허용된 MIME 타입 목록
     */
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );

    /**
     * Presigned URL을 생성합니다.
     *
     * @param fileName 업로드할 파일명
     * @param fileSize 파일 크기 (바이트 단위)
     * @param mimeType 파일의 MIME 타입
     * @param type 이미지 사용 용도 (profile_image, post_image 등)
     * @param boardType 게시판 타입 (post_image 타입인 경우에만 사용)
     * @return Presigned URL 정보가 포함된 응답 DTO
     * @throws S3Exception MIME 타입이 지원되지 않거나, 파일 크기가 제한을 초과하는 경우,
     *                   또는 S3 서비스 연결 오류 발생 시
     */
    public PresignedUrlResponseDto generatePresignedUrl(
            String fileName, Long fileSize, String mimeType, String type, Post.BoardType boardType) {
        // 파일 타입 검증
        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new S3Exception(S3ErrorCode.UNSUPPORTED_IMAGE_FORMAT);
        }

        // 파일 크기 검증
        if (fileSize > maxFileSize) {
            throw new S3Exception(S3ErrorCode.FILE_SIZE_EXCEEDED);
        }

        try {
            // 파일 경로 및 이름 생성 (타입에 따라 폴더 구분)
            String objectKey = generateObjectKey(type, fileName, boardType);

            // 만료 시간 설정
            Date expiration = getExpirationTime();

            // Presigned URL 생성 요청 객체 생성
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objectKey)
                    .withMethod(HttpMethod.PUT)
                    .withExpiration(expiration);

            // Content-Type 설정
            generatePresignedUrlRequest.addRequestParameter("Content-Type", mimeType);

            // Presigned URL 생성
            URL presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

            // 이미지 접근 URL 생성
            String imageUrl = generateImageUrl(objectKey);

            return PresignedUrlResponseDto.builder()
                    .presingedUrl(presignedUrl.toString())
                    .imageUrl(imageUrl)
                    .expiresIn(expirationTime)
                    .build();
        } catch (Exception e) {
            log.error("S3 Presigned URL 생성 실패", e);
            throw new S3Exception(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Presigned URL을 생성합니다. (기존 메서드 - 하위 호환성 유지)
     */
    public PresignedUrlResponseDto generatePresignedUrl(
            String fileName, Long fileSize, String mimeType, String type) {
        return generatePresignedUrl(fileName, fileSize, mimeType, type, null);
    }

    /**
     * 이미지 URL이 유효한지 확인합니다 (버킷 내 경로인지).
     *
     * @param imageUrl 확인할 이미지 URL
     * @return 유효한 URL이면 true, 그렇지 않으면 false
     */
    public boolean isValidImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }

        String bucketDomain = bucketName + ".s3." + amazonS3.getRegion() + ".amazonaws.com";
        return imageUrl.contains(bucketDomain);
    }

    /**
     * S3에서 객체를 삭제합니다.
     *
     * @param imageUrl 삭제할 이미지의 URL
     * @throws S3Exception S3 객체 삭제 중 오류 발생 시
     */
    public void deleteObject(String imageUrl) {
        try {
            String objectKey = extractObjectKeyFromUrl(imageUrl);
            amazonS3.deleteObject(bucketName, objectKey);
            log.info("S3 객체 삭제 완료: {}", objectKey);
        } catch (Exception e) {
            log.error("S3 객체 삭제 실패: {}", imageUrl, e);
            throw new S3Exception(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 타입별 S3 객체 키를 생성합니다. (기존 메서드 - 하위 호환성 유지)
     *
     * @param type 이미지 타입 (profile_image, post_image 등)
     * @param originalFilename 원본 파일명
     * @return 생성된 객체 키
     */
    private String generateObjectKey(String type, String originalFilename) {
        return generateObjectKey(type, originalFilename, null);
    }

    /**
     * 타입별 S3 객체 키를 생성합니다.
     *
     * @param type 이미지 타입 (profile_image, post_image 등)
     * @param originalFilename 원본 파일명
     * @param boardType 게시판 타입 (post_image 타입인 경우에만 사용)
     * @return 생성된 객체 키
     */
    private String generateObjectKey(String type, String originalFilename, Post.BoardType boardType) {
        // 확장자 추출
        String extension = getFileExtension(originalFilename);
        // UUID 생성
        String uuid = UUID.randomUUID().toString();

        // 타입에 따른 경로 설정
        String path;
        if ("profile_image".equals(type)) {
            path = "profiles";
        } else if ("post_image".equals(type)) {
            // 게시판 타입에 따라 세부 경로 지정
            String boardPath;
            if (boardType == null) {
                boardPath = "general";
            } else {
                switch (boardType) {
                    case ALL:
                        boardPath = "all";
                        break;
                    case PANGYO_1:
                        boardPath = "pangyo1";
                        break;
                    case PANGYO_2:
                        boardPath = "pangyo2";
                        break;
                    case JEJU_1:
                        boardPath = "jeju1";
                        break;
                    case JEJU_2:
                        boardPath = "jeju2";
                        break;
                    case JEJU_3:
                        boardPath = "jeju3";
                        break;
                    default:
                        boardPath = "others";
                }
            }
            path = "posts/" + boardPath;
        } else {
            path = "others";
        }

        return String.format("%s/%s.%s", path, uuid, extension);
    }

    /**
     * 파일 확장자를 추출합니다.
     *
     * @param fileName 파일명
     * @return 파일 확장자
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "jpg";  // 기본 확장자
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * Presigned URL의 만료 시간을 설정합니다.
     *
     * @return 만료 시간
     */
    private Date getExpirationTime() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += expirationTime * 1000; // 초 -> 밀리초 변환
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    /**
     * 객체 키로부터 이미지 URL을 생성합니다.
     *
     * @param objectKey S3 객체 키
     * @return 이미지 접근 URL
     */
    private String generateImageUrl(String objectKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, amazonS3.getRegion(), objectKey);
    }

    /**
     * URL에서 S3 객체 키를 추출합니다.
     *
     * @param imageUrl 이미지 URL
     * @return 추출된 객체 키
     */
    private String extractObjectKeyFromUrl(String imageUrl) {
        String baseUrl = String.format("https://%s.s3.%s.amazonaws.com/",
                bucketName, amazonS3.getRegion());
        return imageUrl.substring(baseUrl.length());
    }
}