package com.kakaobase.snsapp.global.error.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeneralErrorCode implements BaseErrorCode {

    INVALID_QUERY_PARAMETER(HttpStatus.BAD_REQUEST, "invalid_query_parameter", "요청 쿼리 파라미터가 유효하지 않습니다.", null),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "missing_required_field", "필수 입력 값이 누락되었습니다.", null),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, "invalid_format", "입력 형식이 잘못되었습니다.", null),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "resource_not_found", "요청한 리소스를 찾을 수 없습니다.", null),
    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "rate_limit_exceeded", "요청이 너무 많습니다.", null),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "internal_server_error", "서버 내부 오류가 발생했습니다.", null);

    private final HttpStatus status;
    private final String error;
    private final String message;
    private final String field;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getError() {
        return error;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getField() {
        return field; // 기본은 null, 상황에 따라 덮어쓸 수 있음
    }
}
