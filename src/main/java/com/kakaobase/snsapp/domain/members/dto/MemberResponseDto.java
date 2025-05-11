package com.kakaobase.snsapp.domain.members.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 회원 응답 DTO
 */
public class MemberResponseDto {

    @Schema(description = "회원 프로필 응답 DTO")
    public record Profile(
            @Schema(description = "회원 ID", example = "1")
            Long id,

            @Schema(description = "이메일", example = "example@domain.com")
            String email,

            @Schema(description = "이름", example = "홍길동")
            String name,

            @Schema(description = "닉네임", example = "gildong")
            String nickname,

            @Schema(description = "기수명", example = "PANGYO_1")
            String className,

            @Schema(description = "프로필 이미지 URL", example = "https://example.com/images/profile.jpg")
            String profileImgUrl,

            @Schema(description = "GitHub URL", example = "https://github.com/gildong")
            String githubUrl,

            @Schema(description = "팔로워 수", example = "42")
            Integer followerCount,

            @Schema(description = "팔로잉 수", example = "38")
            Integer followingCount,

            @Schema(description = "본인 여부", example = "true")
            Boolean isMe,

            @Schema(description = "팔로우 여부 (로그인한 사용자가 조회 대상을 팔로우했는지)", example = "false")
            Boolean isFollowing
    ) {}

    @Schema(description = "이메일 인증 코드 검증 실패 응답 DTO")
    public record EmailVerificationFailure(
            @Schema(description = "에러 코드", example = "email_code_invalid")
            String error,

            @Schema(description = "에러 메시지", example = "인증 코드가 유효하지 않습니다.")
            String message,

            @Schema(description = "에러 필드", example = "code")
            String field,

            @Schema(description = "실패 횟수", example = "2")
            Integer failCount,

            @Schema(description = "최대 실패 횟수", example = "3")
            Integer maxFailCount
    ) {}

    @Schema(description = "로그아웃 응답 DTO (3회 실패 시)")
    public record EmailVerificationFailureLogout(
            @Schema(description = "에러 코드", example = "email_code_fail_logout")
            String error,

            @Schema(description = "에러 메시지", example = "인증에 3회 실패하여 로그아웃되었습니다.")
            String message,

            @Schema(description = "에러 필드", example = "code")
            String field,

            @Schema(description = "실패 횟수", example = "3")
            Integer failCount,

            @Schema(description = "최대 실패 횟수", example = "3")
            Integer maxFailCount
    ) {}
}