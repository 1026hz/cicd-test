package com.kakaobase.snsapp.domain.auth.exception;

import com.kakaobase.snsapp.global.error.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 인증 도메인 특화 에러 코드를 정의합니다.
 * 로그인, 토큰, 인증 관련 에러를 포함하고 있습니다.
 * Spring Security 관련 에러는 SecurityExceptionHandler에서 처리되므로 여기서는
 * 비즈니스 로직 관련 에러만 정의합니다.
 */
@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    // 로그인 관련 에러
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "invalid_password", "비밀번호가 일치하지 않습니다.", "password"),
    USER_DELETED(HttpStatus.NOT_FOUND, "deleted_user", "삭제된 유저입니다.", "userId"),
    USER_BANNED(HttpStatus.FORBIDDEN, "banned_user", "벤처리된 유저입니다.", "userId"),

    // 리프레시 토큰 관련 에러 (Spring Security 외부에서 처리되는 경우)
    REFRESH_TOKEN_MISSING(HttpStatus.BAD_REQUEST, "refresh_token_missing", "refreshToken이 쿠키에 존재하지 않습니다.", "refreshToken"),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "refresh_token_invalid", "refreshToken이 유효하지 않습니다.", "refreshToken"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "refresh_token_invalid", "refreshToken이 만료되었습니다.", "refreshToken"),
    REFRESH_TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, "refresh_token_invalid", "사용할 수 없는 refreshToken입니다.", "refreshToken");

    private final HttpStatus status;
    private final String error;
    private final String message;
    private final String field;

}