package com.kakaobase.snsapp.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * API 응답의 표준 형식을 정의하는 클래스입니다.
 * 성공 응답과 실패 응답에 대한 다른 포맷을 제공합니다.
 * @param <T> 응답 데이터의 타입
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponse<T> {

    /**
     * 응답 메시지입니다.
     * 성공과 실패 응답 모두에 포함됩니다.
     */
    private String message;

    /**
     * 응답 데이터입니다.
     * 성공 응답에만 포함됩니다.
     */
    private T data;

    /**
     * 에러 코드입니다.
     * 실패 응답에만 포함됩니다.
     */
    private String error;

    /**
     * 에러가 발생한 필드입니다.
     * 실패 응답에서 필드 관련 에러가 있을 때만 포함됩니다.
     */
    private String field;

    /**
     * 성공 응답을 생성합니다.
     *
     * @param message 성공 메시지
     * @param data 응답 데이터
     * @param <T> 데이터의 타입
     * @return 성공 응답 객체
     */
    public static <T> CustomResponse<T> success(String message, T data) {
        return CustomResponse.<T>builder()
                .message(message)
                .data(data)
                .build();
    }

    /**
     * 간단한 성공 응답을 생성합니다.
     *
     * @param message 성공 메시지
     * @return 데이터가 없는 성공 응답 객체
     */
    public static CustomResponse<Void> success(String message) {
        return success(message, null);
    }

    /**
     * 실패 응답을 생성합니다.
     *
     * @param error 에러 코드
     * @param message 에러 메시지
     * @param field 에러가 발생한 필드 (optional)
     * @return 실패 응답 객체
     */
    public static CustomResponse<Void> failure(String error, String message, String field) {
        return CustomResponse.<Void>builder()
                .error(error)
                .message(message)
                .field(field)
                .build();
    }

    /**
     * 필드 정보가 없는 실패 응답을 생성합니다.
     * @param error 에러 코드
     * @param message 에러 메시지
     * @return 실패 응답 객체
     */
    public static CustomResponse<Void> failure(String error, String message) {
        return failure(error, message, null);
    }
}