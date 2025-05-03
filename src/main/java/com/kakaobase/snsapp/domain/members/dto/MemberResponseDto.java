package com.kakaobase.snsapp.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "회원 응답 DTO")
public class MemberResponseDto {

    @Schema(description = "회원 프로필 응답 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Profile {
        @Schema(description = "회원 ID", example = "1")
        private Long id;

        @Schema(description = "이메일", example = "example@domain.com")
        private String email;

        @Schema(description = "이름", example = "홍길동")
        private String name;

        @Schema(description = "닉네임", example = "gildong")
        private String nickname;

        @Schema(description = "기수명", example = "PANGYO_1")
        private String className;

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/images/profile.jpg")
        private String profileImgUrl;

        @Schema(description = "GitHub URL", example = "https://github.com/gildong")
        private String githubUrl;

        @Schema(description = "팔로워 수", example = "42")
        private Integer followerCount;

        @Schema(description = "팔로잉 수", example = "38")
        private Integer followingCount;

        @Schema(description = "본인 여부", example = "true")
        private Boolean isMe;

        @Schema(description = "팔로우 여부 (로그인한 사용자가 조회 대상을 팔로우했는지)", example = "false")
        private Boolean isFollowing;
    }

    @Schema(description = "이메일 인증 코드 검증 실패 응답 DTO")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailVerificationFailure {
        @Schema(description = "에러 코드", example = "email_code_invalid")
        private String error;

        @Schema(description = "에러 메시지", example = "인증 코드가 유효하지 않습니다.")
        private String message;

        @Schema(description = "에러 필드", example = "code")
        private String field;

        @Schema(description = "실패 횟수", example = "2")
        private Integer failCount;

        @Schema(description = "최대 실패 횟수", example = "3")
        private Integer maxFailCount;
    }

    @Schema(description = "로그아웃 응답 DTO (3회 실패 시)")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailVerificationFailureLogout {
        @Schema(description = "에러 코드", example = "email_code_fail_logout")
        private String error;

        @Schema(description = "에러 메시지", example = "인증에 3회 실패하여 로그아웃되었습니다.")
        private String message;

        @Schema(description = "에러 필드", example = "code")
        private String field;

        @Schema(description = "실패 횟수", example = "3")
        private Integer failCount;

        @Schema(description = "최대 실패 횟수", example = "3")
        private Integer maxFailCount;
    }
}