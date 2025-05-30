package com.kakaobase.snsapp.domain.members.controller;

import com.kakaobase.snsapp.domain.members.dto.MemberRequestDto;
import com.kakaobase.snsapp.domain.members.service.MemberService;
import com.kakaobase.snsapp.global.common.email.service.EmailVerificationService;
import com.kakaobase.snsapp.global.common.response.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 회원 관련 API 컨트롤러
 */
@Slf4j
@Tag(name = "회원 API", description = "회원 관련 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = CustomResponse.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 입력값"),
            @ApiResponse(responseCode = "401", description = "이메일 인증 미완료"),
            @ApiResponse(responseCode = "409", description = "이미 등록된 이메일 또는 닉네임")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomResponse<Void> signUp(
            @Parameter(description = "회원가입 정보", required = true)
            @Valid @RequestBody MemberRequestDto.SignUp request) {

        memberService.signUp(request);
        return CustomResponse.success("회원가입이 완료되었습니다.");
    }
}