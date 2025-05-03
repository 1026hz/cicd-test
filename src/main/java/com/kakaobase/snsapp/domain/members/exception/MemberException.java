package com.kakaobase.snsapp.domain.members.exception;

import com.kakaobase.snsapp.global.error.exception.CustomException;

/**
 * 회원 도메인에서 발생하는 예외를 처리하는 커스텀 예외 클래스입니다.
 * 회원 관련 비즈니스 예외는 모두 이 클래스를 사용합니다.
 */
public class MemberException extends CustomException {

    /**
     * 회원 에러 코드를 받아 MemberException을 생성합니다.
     *
     * @param errorCode 회원 에러 코드 (MemberErrorCode)
     */
    public MemberException(MemberErrorCode errorCode) {
        super(errorCode);
    }

}