package com.kakaobase.snsapp.global.common.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * 이메일 발송을 담당하는 클래스입니다.
 * Spring Mail을 사용하여 SMTP를 통해 이메일을 전송합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender javaMailSender;

    /**
     * 일반 이메일을 전송합니다.
     *
     * @param to      수신자 이메일
     * @param subject 이메일 제목
     * @param content 이메일 내용
     */
    public void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            javaMailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * 이메일 인증 코드를 전송합니다.
     *
     * @param to           수신자 이메일
     * @param code         인증 코드
     * @param purpose      인증 목적 (sign-up, password-reset)
     */
    public void sendVerificationEmail(String to, String code, String purpose) {
        String subject = getSubjectByPurpose(purpose);
        String content = createEmailContent(code, purpose);

        sendEmail(to, subject, content);
    }

    /**
     * 인증 목적에 따른 이메일 제목을 반환합니다.
     */
    private String getSubjectByPurpose(String purpose) {
        return switch (purpose) {
            case "sign-up" -> "[KakaoBase] 회원가입 인증 코드";
            case "password-reset" -> "[KakaoBase] 비밀번호 재설정 인증 코드";
            default -> "[KakaoBase] 인증 코드";
        };
    }

    /**
     * 인증 코드 이메일 내용을 생성합니다.
     */
    private String createEmailContent(String code, String purpose) {
        StringBuilder content = new StringBuilder();
        content.append("안녕하세요.\n\n");

        if ("sign-up".equals(purpose)) {
            content.append("KakaoBase 회원가입을 위한 인증 코드를 발송드립니다.\n\n");
        } else if ("password-reset".equals(purpose)) {
            content.append("비밀번호 재설정을 위한 인증 코드를 발송드립니다.\n\n");
        }

        content.append("인증 코드: ").append(code).append("\n\n")
                .append("이 코드는 10분간 유효합니다.\n\n")
                .append("안녕히 계세요.\n")
                .append("KakaoBase 드림");

        return content.toString();
    }
}