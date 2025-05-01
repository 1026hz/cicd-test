package com.kakaobase.snsapp.global.error.code;

import com.kakaobase.snsapp.global.common.dto.response.CustomResponse;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드의 기본 인터페이스입니다.
 * 모든 에러 코드는 이 인터페이스를 구현해야 합니다.
 */
public interface BaseErrorCode {

    /**
     * HTTP 상태 코드를 반환합니다.
     * @return HTTP 상태 코드
     */
    HttpStatus getStatus();

    /**
     * 에러 코드를 반환합니다.
     * @return 에러 코드 문자열
     */
    String getError();

    /**
     * 에러 메시지를 반환합니다.
     * @return 에러 메시지
     */
    String getMessage();

    /**
     * 에러가 발생한 필드를 반환합니다.
     * @return 필드명 (없을 경우 null)
     */
    String getField();

    /**
     * 에러 응답 객체를 생성합니다.
     * @return 에러 응답 객체
     */
    default CustomResponse<Void> getErrorResponse() {
        return CustomResponse.failure(getError(), getMessage(), getField());
    }
}