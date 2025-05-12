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
}