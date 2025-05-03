package com.kakaobase.snsapp.domain.members.exception;

import com.kakaobase.snsapp.global.error.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 회원 도메인 관련 에러 코드를 정의합니다.
 */
@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    // 회원가입 관련 에러 (400 Bad Request)
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "missing_required_field", "필수 입력값이 누락되었습니다.", null),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, "invalid_format", "입력값의 형식이 올바르지 않습니다.", null),

    // 회원가입 관련 에러 (401 Unauthorized)
    EMAIL_VERIFICATION_FAILED(HttpStatus.UNAUTHORIZED, "email_verification_failed", "이메일 인증이 완료되지 않았습니다.", "email"),

    // 회원가입 관련 에러 (404 Not Found)
    NAME_NOT_REGISTERED_FOR_CLASS(HttpStatus.NOT_FOUND, "resource_not_found", "해당 기수에 등록된 이름이 아닙니다.", "name"),

    // 회원가입 관련 에러 (409 Conflict)
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "resource_already_exists", "이미 가입된 이메일입니다.", "email"),

    // 회원 조회 관련 에러
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "resource_not_found", "해당 회원을 찾을 수 없습니다.", "userId"),

    // 접근 권한 관련 에러
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "forbidden", "접근 권한이 없습니다.", null),

    // 비밀번호 관련 에러
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "invalid_password", "비밀번호가 일치하지 않습니다.", "password"),
    SAME_AS_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "same_as_current_password", "새 비밀번호는 현재 비밀번호와 달라야 합니다.", "newPassword"),

    // 기타 형식 검증 에러
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "invalid_format", "이메일 형식이 올바르지 않습니다.", "email"),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "invalid_format", "비밀번호 형식이 올바르지 않습니다.", "password"),
    INVALID_GITHUB_URL_FORMAT(HttpStatus.BAD_REQUEST, "invalid_format", "GitHub URL 형식이 올바르지 않습니다.", "githubUrl"),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "invalid_format", "닉네임 형식이 올바르지 않습니다.", "nickname");

    private final HttpStatus status;
    private final String error;
    private final String message;
    private final String field;
}