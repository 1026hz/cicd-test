package com.kakaobase.snsapp.domain.members.service;

import com.kakaobase.snsapp.domain.members.exception.MemberErrorCode;
import com.kakaobase.snsapp.domain.members.exception.MemberException;
import com.kakaobase.snsapp.domain.members.repository.MemberRepository;
import com.kakaobase.snsapp.global.common.email.EmailSender;
import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import com.kakaobase.snsapp.global.error.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 이메일 인증 서비스
 * 임시로 Map을 사용하여 인증 코드를 관리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailSender emailSender;
    private final MemberRepository memberRepository;

    // 임시 저장소 - V2에서 Redis로 변경 예정
    private final Map<String, VerificationData> verificationStore = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    private static final int CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 3;

    /**
     * 인증 코드 전송
     */
    public void sendVerificationCode(String email, String purpose, Authentication authentication) {
        validateEmailRequest(email, purpose, authentication);

        String code = generateCode();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);

        VerificationData data = new VerificationData(code, expirationTime, 0);
        verificationStore.put(email, data);

        emailSender.sendVerificationEmail(email, code, purpose);
        log.info("Verification code sent to: {}, purpose: {}", email, purpose);
    }

    /**
     * 인증 코드 검증
     */
    public void verifyCode(String email, String code, Authentication authentication) {
        VerificationData data = verificationStore.get(email);

        if (data == null) {
            throw new MemberException(MemberErrorCode.EMAIL_CODE_EXPIRED);
        }

        if (LocalDateTime.now().isAfter(data.getExpirationTime())) {
            verificationStore.remove(email);
            throw new MemberException(MemberErrorCode.EMAIL_CODE_EXPIRED);
        }

        if (!data.getCode().equals(code)) {
            data.incrementAttempts();

            if (data.getAttempts() >= MAX_ATTEMPTS) {
                verificationStore.remove(email);
                throw new MemberException(MemberErrorCode.EMAIL_CODE_FAIL_LOGOUT);
            }

            throw new MemberException(MemberErrorCode.EMAIL_CODE_INVALID);
        }

        // 인증 성공 - 인증 완료 표시
        data.setVerified(true);
        log.info("Email verified successfully: {}", email);
    }

    /**
     * 이메일 인증 상태 확인
     */
    public boolean isEmailVerified(String email) {
        VerificationData data = verificationStore.get(email);
        return data != null && data.isVerified();
    }

    /**
     * 이메일 요청 유효성 검증
     */
    private void validateEmailRequest(String email, String purpose, Authentication authentication) {
        if ("password-reset".equals(purpose)) {
            if (authentication == null) {
                throw new CustomException(MemberErrorCode.UNAUTHORIZED_ACCESS);
            }

            if (!memberRepository.existsByEmail(email)) {
                throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
            }
        } else if ("sign-up".equals(purpose)) {
            if (memberRepository.existsByEmail(email)) {
                throw new MemberException(GeneralErrorCode.RESOURCE_ALREADY_EXISTS, "email");
            }
        }
    }

    /**
     * 6자리 인증 코드 생성
     */
    private String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 인증 데이터 내부 클래스
     */
    @Getter
    private static class VerificationData {
        private final String code;
        private final LocalDateTime expirationTime;
        private int attempts;
        private boolean verified;

        public VerificationData(String code, LocalDateTime expirationTime, int attempts) {
            this.code = code;
            this.expirationTime = expirationTime;
            this.attempts = attempts;
            this.verified = false;
        }

        public void incrementAttempts() {
            this.attempts++;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }
    }
}