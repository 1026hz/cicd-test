package com.kakaobase.snsapp.domain.posts.exception;

import com.kakaobase.snsapp.global.error.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostErrorCode implements BaseErrorCode {

    // 게시글 내용 관련 에러
    EMPTY_POST_CONTENT(HttpStatus.BAD_REQUEST, "empty_post_content", "글 내용, 이미지, 유튜브 링크 중 하나는 필수입니다.", null),
    CONTENT_LENGTH_EXCEEDED(HttpStatus.BAD_REQUEST, "content_length_exceeded", "게시글 본문은 최대 2000자까지 작성할 수 있습니다.", "content"),

    // 권한 관련 에러
    POST_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "forbidden", "본인의 게시글만 삭제할 수 있습니다.", "postId"),
    POST_TYPE_FORBIDDEN(HttpStatus.FORBIDDEN, "forbidden", "해당 게시판에 접근할 권한이 없습니다.", "postType"),

    // 이미지 관련 에러
    INVALID_IMAGE_URL(HttpStatus.BAD_REQUEST, "invalid_image_url", "유효하지 않은 이미지 주소입니다.", "image_url"),

    // 유튜브 요약본 관련 에러
    INVALID_YOUTUBE_URL(HttpStatus.BAD_REQUEST, "invalid_format", "유튜브 링크가 유효하지 않습니다.", "youtube_url"),
    YOUTUBE_SUBTITLE_NOT_FOUND(HttpStatus.UNPROCESSABLE_ENTITY, "youtube_subtitle_not_found", "해당 유튜브 영상에 자막이 존재하지 않습니다.", "youtube_url"),
    UNSUPPORTED_SUBTITLE_LANGUAGE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "unsupported_subtitle_language", "해당 YouTube 영상의 자막이 한국어 또는 영어를 지원하지 않습니다", "youtube_url"),
    YOUTUBE_VIDEO_PRIVATE(HttpStatus.FORBIDDEN, "video_private", "해당 YouTube 영상은 비공개 동영상입니다.", "youtube_url"),
    YOUTUBE_VIDEO_NOT_FOUND(HttpStatus.NOT_FOUND, "video_not_found", "존재하지 않는 youtube url입니다.", "youtube_url"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "internal_server_error", "서버 내부 오류가 발생했습니다.", null),

    // 좋아요 관련 에러
    ALREADY_LIKED(HttpStatus.CONFLICT, "state_conflict", "이미 좋아요한 게시글입니다.", null),
    ALREADY_UNLIKED(HttpStatus.CONFLICT, "state_conflict", "이미 좋아요를 취소한 게시글입니다.", null);

    private final HttpStatus status;
    private final String error;
    private final String message;
    private final String field;
}