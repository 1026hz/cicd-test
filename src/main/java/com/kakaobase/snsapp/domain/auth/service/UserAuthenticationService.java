package com.kakaobase.snsapp.domain.auth.service;

import com.kakaobase.snsapp.domain.auth.exception.AuthErrorCode;
import com.kakaobase.snsapp.domain.auth.exception.AuthException;
import com.kakaobase.snsapp.domain.auth.principal.CustomUserDetails;
import com.kakaobase.snsapp.domain.auth.principal.CustomUserDetailsService;
import com.kakaobase.snsapp.domain.auth.util.CookieUtil;

import com.kakaobase.snsapp.domain.members.entity.Member;
import com.kakaobase.snsapp.domain.members.repository.MemberRepository;
import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import com.kakaobase.snsapp.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 인증 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthenticationService {

    private final MemberRepository memberRepository;
    private final SecurityTokenManager securityTokenManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    /**
     * 사용자 로그인 처리 및 인증 토큰 발급
     */
    @Transactional
    public TokenWithCookie login(String email, String password, String userAgent) {
        // 1. 이메일로 사용자 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(GeneralErrorCode.RESOURCE_NOT_FOUND, email));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD);
        }


        // 3. CustomUserDetailsService를 사용하여 인증 객체 생성
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserById(member.getId().toString());

        // 4. Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,  // 인증 완료 후에는 credentials는 null
                userDetails.getAuthorities()
        );

        // 5. Refresh Token 발급 및 저장
        String refreshToken = securityTokenManager.createRefreshToken(
                Long.parseLong(userDetails.getId()),
                userAgent
        );

        // 6. 액세스 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);

        // 7. 리프레시 토큰 쿠키 생성
        Cookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(refreshToken);

        return new TokenWithCookie(accessToken, refreshTokenCookie);
    }

    /**
     * 리프레시 토큰을 사용해 새 액세스 토큰 발급
     */
    @Transactional
    public String refreshAuthentication(String refreshToken) {
        if (refreshToken == null) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_MISSING);
        }

        // 1. 리프레시 토큰 검증 및 사용자 ID 추출
        Long userId = securityTokenManager.validateRefreshTokenAndGetUserId(refreshToken);

        // 2. CustomUserDetailsService를 통해 인증 정보 로드
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserById(String.valueOf(userId));

        // 3. 인증 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // 4. 새 액세스 토큰 발급
        return jwtTokenProvider.createAccessToken(authentication);
    }

    /**
     * 로그아웃 처리
     */
    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_MISSING);
        }
        securityTokenManager.revokeRefreshToken(refreshToken);
    }

    /**
     * 다른 모든 디바이스 로그아웃
     */
    @Transactional
    public void logoutOtherDevices(Long memberId, String currentRefreshToken) {
        if (currentRefreshToken == null) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_MISSING);
        }
        securityTokenManager.revokeAllTokensExcept(memberId, currentRefreshToken);
    }


    /**
     * 액세스 토큰과 리프레시 토큰 쿠키를 포함하는 응답 DTO
     */
    public record TokenWithCookie(String accessToken, Cookie refreshTokenCookie) {}
}