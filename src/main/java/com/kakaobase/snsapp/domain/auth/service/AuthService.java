package com.kakaobase.snsapp.domain.auth.service;

import com.kakaobase.snsapp.domain.auth.dto.AuthRequestDto;
import com.kakaobase.snsapp.domain.auth.dto.AuthResponseDto;
import com.kakaobase.snsapp.domain.auth.exception.AuthErrorCode;
import com.kakaobase.snsapp.domain.auth.exception.AuthException;
import com.kakaobase.snsapp.domain.auth.principal.CustomUserDetails;
import com.kakaobase.snsapp.domain.auth.principal.CustomUserDetailsService;
import com.kakaobase.snsapp.domain.auth.util.CookieUtil;

import com.kakaobase.snsapp.domain.members.repository.MemberRepository;
import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import com.kakaobase.snsapp.global.error.exception.CustomException;
import com.kakaobase.snsapp.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 인증 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SecurityTokenManager securityTokenManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * 사용자 로그인 처리 및 인증 토큰 발급
     */
    @Transactional(readOnly = true)
    public AuthResponseDto.LoginResponse login(AuthRequestDto.Login request) {

        String email = request.email();
        String password = request.password();
        CustomUserDetails userDetails;

        //이메일 인증 겸 인증객체 생성
        try{
            userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);
        }catch (UsernameNotFoundException e){
            throw new AuthException(GeneralErrorCode.RESOURCE_NOT_FOUND, email);
        }

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_PASSWORD);
        }

        // 3. 모든 검증이 완료된 후에 인증 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 6. 액세스 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(userDetails);

        log.debug("로그인 사용자 정보: id-{}, nickname-{}, className-{}, imgurl-{}", Long.valueOf(userDetails.getId()), userDetails.getNickname(), userDetails.getClassName(), userDetails.getProfileImgUrl());
        return new AuthResponseDto.LoginResponse(Long.valueOf(userDetails.getId()), userDetails.getNickname(), userDetails.getClassName(), userDetails.getProfileImgUrl(), accessToken);
    }


    //refresh토큰 쿠키 생성
    @Transactional
    public ResponseCookie getRefreshCookie(String providedRefreshToken, String userAgent){

        //login메서드시 생성된 인증객체 반환
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        // 리프레시 토큰이 있다면 기존 토큰 파기
        if (providedRefreshToken != null
                && !providedRefreshToken.isBlank()
                && providedRefreshToken.length() > 20) {
            securityTokenManager.revokeRefreshToken(providedRefreshToken);
        }

        String refreshToken = securityTokenManager.createRefreshToken(
                Long.parseLong(userDetails.getId()),
                userAgent
        );

        return cookieUtil.createRefreshTokenCookie(refreshToken);
    }

    /**
     * 리프레시 토큰을 사용해 새 액세스 토큰 발급
     */
    @Transactional
    public String getAccessToken(String providedRefreshToken) {

        // 토큰이 없으면 예외 발생
        if (providedRefreshToken == null
                || providedRefreshToken.isBlank()
                || providedRefreshToken.length() < 20)
        {
            throw new CustomException(AuthErrorCode.REFRESH_TOKEN_MISSING);
        }

        // 1. 리프레시 토큰 검증 및 사용자 ID 추출
        Long userId = securityTokenManager.validateRefreshTokenAndGetUserId(providedRefreshToken);

        // 2. CustomUserDetailsService를 통해 인증 정보 로드
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserById(String.valueOf(userId));

        // 4. 새 액세스 토큰 발급
        return jwtTokenProvider.createAccessToken(userDetails);
    }

    /**
     * 로그아웃 처리
     */
    @Transactional
    public ResponseCookie logout(String providedRefreshToken) {
        // 토큰이 있다면 기존 토큰 파기
        if (providedRefreshToken != null
                && !providedRefreshToken.isBlank()
                && providedRefreshToken.length() > 20) {
            securityTokenManager.revokeRefreshToken(providedRefreshToken);
        }
        return cookieUtil.createEmptyRefreshCookie();
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
}