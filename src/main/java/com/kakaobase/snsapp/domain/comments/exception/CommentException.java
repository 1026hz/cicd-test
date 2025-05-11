package com.kakaobase.snsapp.domain.comments.exception;

import com.kakaobase.snsapp.global.error.code.BaseErrorCode;
import com.kakaobase.snsapp.global.error.exception.CustomException;

/**
 * 게시글 도메인에서 발생하는 예외를 처리하는 클래스입니다.
 * CustomException을 상속받아 게시글 관련 예외를 특화하여 처리합니다.
 */
public class CommentException extends CustomException {

    /**
     * 게시글 관련 에러 코드를 받아 PostException을 생성합니다.
     *
     * @param errorCode 에러 코드 (PostErrorCode)
     */
    public CommentException(BaseErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 게시글 관련 에러 코드와 필드를 받아 PostException을 생성합니다.
     *
     * @param errorCode 에러 코드 (PostErrorCode)
     * @param field 에러가 발생한 필드
     */
    public CommentException(BaseErrorCode errorCode, String field) {
        super(errorCode, field);
    }

    /**
     * 게시글 관련 에러 코드, 필드, 추가 메시지를 받아 PostException을 생성합니다.
     *
     * @param errorCode 에러 코드 (PostErrorCode)
     * @param field 에러가 발생한 필드
     * @param additionalMessage 추가 메시지
     */
    public CommentException(BaseErrorCode errorCode, String field, String additionalMessage) {
        super(errorCode, field, additionalMessage);
    }

    /**
     * 특정 게시글 ID에 대한 에러를 생성하는 편의 메서드
     *
     * @param errorCode 에러 코드
     * @param postId 게시글 ID
     * @return 게시글 ID 정보가 포함된 예외
     */
    public static CommentException forPostId(BaseErrorCode errorCode, Long postId) {
        return new CommentException(errorCode, "postId", "게시글 ID: " + postId);
    }

    /**
     * 특정 게시판 타입에 대한 에러를 생성하는 편의 메서드
     *
     * @param errorCode 에러 코드
     * @param postType 게시판 타입
     * @return 게시판 타입 정보가 포함된 예외
     */
    public static CommentException forPostType(BaseErrorCode errorCode, String postType) {
        return new CommentException(errorCode, "postType", "게시판 타입: " + postType);
    }
}