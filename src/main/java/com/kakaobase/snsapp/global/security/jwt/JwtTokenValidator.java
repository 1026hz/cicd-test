package com.kakaobase.snsapp.global.security.jwt;

import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import com.kakaobase.snsapp.global.error.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰의 유효성을 검증하는 클래스입니다.
 * 토큰의 서명 검증, 만료 여부 확인, 구조적 유효성 검사 등을 수행합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenValidator {

    private final JwtUtil jwtUtil;

    /**
     * JWT 토큰의 유효성을 검증합니다.
     * 유효하지 않은 경우 UNAUTHORIZED 에러 코드와 함께 CustomException을 발생시킵니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효하면 true
     * @throws CustomException 토큰이 유효하지 않을 경우 발생
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtUtil.getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다: {}", e.getMessage());
            throw new CustomException(GeneralErrorCode.UNAUTHORIZED, "토큰이 만료되었습니다.");
        } catch (SignatureException e) {
            log.error("유효하지 않은 JWT 서명입니다: {}", e.getMessage());
            throw new CustomException(GeneralErrorCode.UNAUTHORIZED, "토큰 서명이 유효하지 않습니다.");
        } catch (MalformedJwtException e) {
            log.error("잘못된 형식의 JWT 토큰입니다: {}", e.getMessage());
            throw new CustomException(GeneralErrorCode.UNAUTHORIZED, "잘못된 형식의 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
            throw new CustomException(GeneralErrorCode.UNAUTHORIZED, "지원되지 않는 토큰입니다.");
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT 토큰이 유효하지 않습니다: {}", e.getMessage());
            throw new CustomException(GeneralErrorCode.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }
    }
}