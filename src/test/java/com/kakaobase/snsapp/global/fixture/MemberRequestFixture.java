package com.kakaobase.snsapp.global.fixture;

import com.kakaobase.snsapp.domain.members.dto.MemberRequestDto;

public class MemberRequestFixture {

    // === SignUp Fixtures ===

    public static MemberRequestDto.SignUp validSignUp() {
        return new MemberRequestDto.SignUp(
                "test@kakao.com",
                "Test1234!",
                "홍길동",
                "gildong",
                "PANGYO_1",
                "https://github.com/gildong"
        );
    }

    public static MemberRequestDto.SignUp duplicateEmailSignUp() {
        return new MemberRequestDto.SignUp(
                "duplicate@kakao.com",
                "Test1234!",
                "홍길동",
                "gildong",
                "PANGYO_1",
                "https://github.com/gildong"
        );
    }

    public static MemberRequestDto.SignUp notVerifiedEmailSignUp() {
        return new MemberRequestDto.SignUp(
                "notverified@kakao.com",
                "Test1234!",
                "홍길동",
                "gildong",
                "PANGYO_1",
                "https://github.com/gildong"
        );
    }

    public static MemberRequestDto.SignUp customEmailSignUp(String email) {
        return new MemberRequestDto.SignUp(
                email,
                "Test1234!",
                "홍길동",
                "gildong",
                "PANGYO_1",
                "https://github.com/gildong"
        );
    }

    public static MemberRequestDto.SignUp customNicknameSignUp(String nickname) {
        return new MemberRequestDto.SignUp(
                "test@kakao.com",
                "Test1234!",
                "홍길동",
                nickname,
                "PANGYO_1",
                "https://github.com/gildong"
        );
    }

    // === EmailVerificationRequest Fixtures ===

    public static MemberRequestDto.EmailVerificationRequest signUpEmailVerification() {
        return new MemberRequestDto.EmailVerificationRequest(
                "verify@kakao.com",
                "sign-up"
        );
    }

    public static MemberRequestDto.EmailVerificationRequest passwordResetEmailVerification() {
        return new MemberRequestDto.EmailVerificationRequest(
                "verify@kakao.com",
                "password-reset"
        );
    }

    public static MemberRequestDto.EmailVerificationRequest customEmailVerification(String email, String purpose) {
        return new MemberRequestDto.EmailVerificationRequest(email, purpose);
    }

    // === EmailVerification Fixtures ===

    public static MemberRequestDto.EmailVerification validEmailVerification() {
        return new MemberRequestDto.EmailVerification(
                "verify@kakao.com",
                "123456"
        );
    }

    public static MemberRequestDto.EmailVerification invalidCodeEmailVerification() {
        return new MemberRequestDto.EmailVerification(
                "verify@kakao.com",
                "999999"
        );
    }

    public static MemberRequestDto.EmailVerification expiredCodeEmailVerification() {
        return new MemberRequestDto.EmailVerification(
                "verify@kakao.com",
                "000000"
        );
    }
}