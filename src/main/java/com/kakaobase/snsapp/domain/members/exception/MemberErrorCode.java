package com.kakaobase.snsapp.domain.members.exception;

import com.kakaobase.snsapp.global.error.code.BaseErrorCode;
import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 회원 도메인 특화 에러 코드를 정의합니다.
 * 공통 에러 코드는 제거하고 도메인별 특화된 에러만 유지합니다.
 */
@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    // 이메일 인증 관련 에러
    EMAIL_VERIFICATION_FAILED(HttpStatus.UNAUTHORIZED, "email_verification_failed", "이메일 인증이 완료되지 않았습니다.", "email"),
    EMAIL_CODE_INVALID(HttpStatus.BAD_REQUEST, "email_code_invalid", "인증 코드가 유효하지 않습니다.", "code"),
    EMAIL_CODE_FAIL_LOGOUT(HttpStatus.UNAUTHORIZED, "email_code_fail_logout", "인증에 3회 실패하여 로그아웃되었습니다.", "code"),
    EMAIL_CODE_EXPIRED(HttpStatus.GONE, "email_code_expired", "인증 코드가 만료되었습니다.", "code"),

    // 회원 가입/조회 관련 에러
    NAME_NOT_REGISTERED_FOR_CLASS(HttpStatus.NOT_FOUND, "resource_not_found", "해당 기수에 등록된 이름이 아닙니다.", "name"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "resource_not_found", "해당 회원을 찾을 수 없습니다.", "userId"),

    // 비밀번호 관련 특화 에러
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "invalid_password", "비밀번호가 일치하지 않습니다.", "password"),
    SAME_AS_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "same_as_current_password", "새 비밀번호는 현재 비밀번호와 달라야 합니다.", "newPassword"),

    // 권한 관련 에러
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "forbidden", "접근 권한이 없습니다.", null);

    private final HttpStatus status;
    private final String error;
    private final String message;
    private final String field;
}