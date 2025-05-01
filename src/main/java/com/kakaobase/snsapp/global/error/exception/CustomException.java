package com.kakaobase.snsapp.global.error.exception;

import com.kakaobase.snsapp.global.error.code.BaseErrorCode;
import lombok.Getter;

/**
 * 애플리케이션에서 발생하는 커스텀 예외 클래스입니다.
 * 모든 비즈니스 예외는 이 클래스를 상속받아 구현합니다.
 */
@Getter
public class CustomException extends RuntimeException {

    /**
     * 예외와 관련된 에러 코드입니다.
     */
    private final transient BaseErrorCode errorCode;

    /**
     * 에러 코드를 받아 CustomException을 생성합니다.
     *
     * @param errorCode 에러 코드 (BaseErrorCodeInterface 구현체)
     */
    public CustomException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 에러 코드와 추가 메시지를 받아 CustomException을 생성합니다.
     *
     * @param errorCode 에러 코드 (BaseErrorCodeInterface 구현체)
     * @param additionalMessage 추가 메시지
     */
    public CustomException(BaseErrorCode errorCode, String additionalMessage) {
        super(errorCode.getMessage() + " " + additionalMessage);
        this.errorCode = errorCode;
    }
}