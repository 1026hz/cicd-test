package com.kakaobase.snsapp.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Auth 도메인의 요청 DTO를 정의하는 클래스입니다.
 * 각 요청 유형에 맞는 중첩 레코드 클래스로 구성되어 있습니다.
 * 로그인, 비밀번호 변경, 프로필 이미지 업로드 등의 요청을 처리합니다.
 */
public class AuthRequestDto {

    /**
     * 로그인 요청을 위한 DTO입니다.
     * 사용자의 이메일과 비밀번호를 검증하고, 성공 시 토큰을 발급받습니다.
     */
    @Schema(description = "로그인 요청 DTO")
    public record Login(
            @Schema(description = "이메일", example = "example@domain.com")
            @NotBlank(message = "이메일은 필수 입력값입니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            String email,

            @Schema(description = "비밀번호", example = "Test1234!")
            @NotBlank(message = "비밀번호는 필수 입력값입니다.")
            String password
    ) {}

    /**
     * 토큰 재발급 요청을 위한 DTO입니다.
     * 클라이언트는 RefreshToken을 쿠키로 전송하므로 요청 본문은 비어있습니다.
     */
    @Schema(description = "토큰 재발급 요청 DTO")
    public record TokenRefresh() {}

    /**
     * 로그아웃 요청을 위한 DTO입니다.
     * 클라이언트는 RefreshToken을 쿠키로 전송하고 AccessToken을 헤더로 전송하므로
     * 요청 본문은 비어있습니다.
     */
    @Schema(description = "로그아웃 요청 DTO")
    public record Logout() {}
}