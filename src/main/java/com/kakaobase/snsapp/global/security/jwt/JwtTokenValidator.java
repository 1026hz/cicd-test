package com.kakaobase.snsapp.global.security.jwt;

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
     * 유효하지 않은 경우 적절한 인증 예외를 발생시킵니다.
     * 이 예외는 JwtAuthenticationFilter에서 캐치되어 SecurityContextHolder를 초기화합니다.
     *
     * @param token 검증할 JWT 토큰
     * @return 토큰이 유효하면 true
     * @throws JwtException 토큰이 유효하지 않을 경우 발생
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtUtil.getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("만료된 JWT 토큰입니다: {}", e.getMessage());
            throw e; // Spring Security에서 처리하도록 예외를 그대로 전파
        } catch (SignatureException e) {
            log.debug("유효하지 않은 JWT 서명입니다: {}", e.getMessage());
            throw e; // Spring Security에서 처리하도록 예외를 그대로 전파
        } catch (MalformedJwtException e) {
            log.debug("잘못된 형식의 JWT 토큰입니다: {}", e.getMessage());
            throw e; // Spring Security에서 처리하도록 예외를 그대로 전파
        } catch (UnsupportedJwtException e) {
            log.debug("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
            throw e; // Spring Security에서 처리하도록 예외를 그대로 전파
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT 토큰이 유효하지 않습니다: {}", e.getMessage());
            throw new JwtException("유효하지 않은 토큰입니다.", e); // 명확한 메시지와 함께 원본 예외를 포함
        }
    }
}