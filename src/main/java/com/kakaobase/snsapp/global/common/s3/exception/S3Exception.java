package com.kakaobase.snsapp.global.common.s3.exception;

import com.kakaobase.snsapp.global.error.code.BaseErrorCode;
import com.kakaobase.snsapp.global.error.exception.CustomException;

/**
 * S3 서비스에서 발생하는 예외를 처리하기 위한 예외 클래스
 *
 * <p>이 클래스는 S3 관련 작업 중 발생할 수 있는 모든 예외를 처리합니다.
 * CustomException을 상속받아 애플리케이션의 전역 예외 처리 메커니즘에 통합됩니다.</p>
 *
 * <p>S3ErrorCode와 함께 사용되어 S3 서비스의 다양한 오류 상황을 표현할 수 있습니다.</p>
 */
public class S3Exception extends CustomException {

    /**
     * S3 예외를 생성합니다.
     *
     * @param errorCode S3 관련 에러 코드 (S3ErrorCode 열거형)
     */
    public S3Exception(BaseErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 특정 필드에 대한 S3 예외를 생성합니다.
     *
     * @param errorCode S3 관련 에러 코드 (S3ErrorCode 열거형)
     * @param field 예외가 발생한 필드
     */
    public S3Exception(BaseErrorCode errorCode, String field) {
        super(errorCode, field);
    }

    /**
     * 추가 메시지를 포함한 S3 예외를 생성합니다.
     *
     * @param errorCode S3 관련 에러 코드 (S3ErrorCode 열거형)
     * @param field 예외가 발생한 필드
     * @param additionalMessage 추가 오류 메시지
     */
    public S3Exception(BaseErrorCode errorCode, String field, String additionalMessage) {
        super(errorCode, field, additionalMessage);
    }
}