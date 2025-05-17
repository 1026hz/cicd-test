package com.kakaobase.snsapp.global.error.exception;

public class AiServerExcepiton extends RuntimeException {

    private final String errorCode;   // AI 서버 응답의 "error"
    private final String message;     // AI 서버 응답의 "message"

    public AiServerExcepiton(String errorCode, String message) {
        super(message);  // RuntimeException의 기본 메시지에도 넣어줌
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}


