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
     * 에러가 발생한 필드를 동적으로 지정할 수 있는 오버라이드 필드입니다.
     */
    private final String overrideField;

    /**
     * 에러 코드를 받아 CustomException을 생성합니다.
     *
     * @param errorCode 에러 코드 (BaseErrorCodeInterface 구현체)
     */
    public CustomException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.overrideField = null;
    }

    /**
     * 에러 코드와 추가 메시지를 받아 CustomException을 생성합니다.
     *
     * @param errorCode 에러 코드 (BaseErrorCodeInterface 구현체)
     * @param overrideField 문제가 발생한 field
     */
    public CustomException(BaseErrorCode errorCode, String overrideField) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.overrideField = overrideField;
    }
    /**
     * 에러 코드와 필드를 받아 CustomException을 생성합니다.
     * field 값을 동적으로 지정할 수 있습니다.
     *
     * @param errorCode 에러 코드 (BaseErrorCodeInterface 구현체)
     * @param field 에러가 발생한 필드
     */
    public CustomException(BaseErrorCode errorCode, String field, String additionalMessage) {
        super(errorCode.getMessage() + " " + additionalMessage);
        this.errorCode = errorCode;
        this.overrideField = field;
    }

    /**
     * 실제 에러가 발생한 필드를 반환합니다.
     * overrideField가 있으면 우선 사용하고, 없으면 errorCode의 필드를 반환합니다.
     *
     * @return 에러가 발생한 필드
     */
    public String getEffectiveField() {
        return overrideField != null ? overrideField : errorCode.getField();
    }
    public BaseErrorCode getErrorCode() {
        return errorCode;
    }
}