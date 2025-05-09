package com.kakaobase.snsapp.domain.comments.exception;

import com.kakaobase.snsapp.global.error.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommentErrorCode implements BaseErrorCode {


    CONTENT_LENGTH_EXCEEDED(HttpStatus.BAD_REQUEST, "content_length_exceeded", "댓글 본문은 최대 2000자까지 작성할 수 있습니다.", "content"),

    // 권한 관련 에러
    POST_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "forbidden", "본인의 댓글만 삭제할 수 있습니다.", "postId"),
    POST_TYPE_FORBIDDEN(HttpStatus.FORBIDDEN, "forbidden", "해당 댓글에 접근할 권한이 없습니다.", "postType"),

    // 좋아요 관련 에러
    ALREADY_LIKED(HttpStatus.CONFLICT, "state_conflict", "이미 좋아요한 댓글입니다.", null),
    ALREADY_UNLIKED(HttpStatus.CONFLICT, "state_conflict", "이미 좋아요를 취소한 댓글입니다.", null);

    private final HttpStatus status;
    private final String error;
    private final String message;
    private final String field;
}