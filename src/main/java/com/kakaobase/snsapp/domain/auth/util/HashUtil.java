package com.kakaobase.snsapp.domain.auth.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 문자열을 SHA-256 방식으로 해싱하는 유틸리티 클래스입니다.
 * - 주로 RefreshToken, 비밀번호, 식별자 등의 단방향 암호화에 사용됩니다.
 * - 순수 함수로 구성되며, 멱등성과 예외 처리를 보장합니다.
 */
public class HashUtil {

    /**
     * 입력 문자열을 SHA-256 방식으로 해싱하여 Hex 문자열로 반환합니다.
     *
     * @param input 해시할 문자열 (예: 원본 RefreshToken)
     * @return SHA-256 해시 결과 (Hex 인코딩된 문자열)
     */
    public static String sha256(String input) {
        try {
            // SHA-256 알고리즘 객체 생성
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 입력 문자열을 UTF-8 인코딩된 바이트 배열로 변환 후 해싱 수행
            byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // 해시 결과를 Hex 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0'); // 한 자리일 경우 앞에 0 붙이기
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 알고리즘이 없을 경우 발생할 수 있는 예외 (일반적으로 발생하지 않음)
            throw new RuntimeException("SHA-256 해싱 중 오류가 발생했습니다.", e);
        }
    }
}
