package com.kakaobase.snsapp.global.error.exception;

public class AiServerException extends RuntimeException {

    private final String errorCode;   // AI 서버 응답의 "error"

    public AiServerException(String errorCode, String message) {
        super(message);  // RuntimeException의 메시지
        this.errorCode = errorCode;
    }

    public AiServerException(String errorCode, String message, Throwable cause) {
        super(message, cause);  // cause로 원인 예외까지 보존
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}


