package com.kakaobase.snsapp.global.security.jwt;

import com.kakaobase.snsapp.global.error.exception.CustomException;
import com.kakaobase.snsapp.global.error.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰 파싱 및 클레임 추출을 담당하는 유틸리티 클래스입니다.
 * 토큰에서 사용자 ID, 역할, 발급 시간, 만료 시간 등의 정보를 추출합니다.
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    /**
     * 설정 값을 주입받아 JWT 유틸리티를 초기화합니다.
     *
     * @param secret JWT 서명에 사용된 비밀키
     */
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * HTTP 요청에서 JWT 토큰을 추출합니다.
     *
     * @param request HTTP 요청
     * @return 추출된 JWT 토큰 (존재하지 않으면 null)
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * JWT 토큰에서 모든 클레임을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 토큰에 포함된 모든 클레임
     * @throws CustomException 토큰 파싱 실패 시 발생
     */
    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
        }
    }

    /**
     * JWT 토큰에서 사용자 ID를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * JWT 토큰에서 사용자 역할을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 사용자 역할
     */
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * JWT 토큰에서 발급 시간을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 토큰 발급 시간
     */
    public Date getIssuedAt(String token) {
        return getClaims(token).getIssuedAt();
    }

    /**
     * JWT 토큰에서 만료 시간을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 토큰 만료 시간
     */
    public Date getExpiration(String token) {
        return getClaims(token).getExpiration();
    }
}