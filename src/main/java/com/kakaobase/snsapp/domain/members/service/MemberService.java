package com.kakaobase.snsapp.domain.members.service;

import com.kakaobase.snsapp.domain.members.converter.MemberConverter;
import com.kakaobase.snsapp.domain.members.dto.MemberRequestDto;
import com.kakaobase.snsapp.domain.members.entity.Member;
import com.kakaobase.snsapp.domain.members.exception.MemberErrorCode;
import com.kakaobase.snsapp.domain.members.exception.MemberException;
import com.kakaobase.snsapp.domain.members.repository.MemberRepository;
import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import com.kakaobase.snsapp.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberConverter memberConverter;
    private final EmailVerificationService emailVerificationService;

    /**
     * 회원 가입 처리
     *
     * @param request 회원가입 요청 DTO
     * @throws MemberException 중복 이메일, 닉네임 또는 인증 미완료 시
     */
    @Transactional
    public void signUp(MemberRequestDto.SignUp request) {
        log.info("회원가입 처리 시작: {}", request.email());

        // 이메일 중복 검사
        if (memberRepository.existsByEmail(request.email())) {
            throw new MemberException(GeneralErrorCode.RESOURCE_ALREADY_EXISTS, "email");
        }

        // 이메일 인증 확인
        if (!emailVerificationService.isEmailVerified(request.email())) {
            throw new MemberException(MemberErrorCode.EMAIL_VERIFICATION_FAILED);
        }

        // Member 엔티티 생성 및 저장
        Member member = memberConverter.toEntity(request);
        memberRepository.save(member);

        log.info("회원가입 완료: {} (ID: {})", request.email(), member.getId());
    }
}