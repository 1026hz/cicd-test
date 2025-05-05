package com.kakaobase.snsapp.global.security.jwt;

import com.kakaobase.snsapp.domain.auth.principal.CustomUserDetails;
import com.kakaobase.snsapp.domain.auth.principal.CustomUserDetailsService;
import com.kakaobase.snsapp.global.error.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 요청에서 JWT 토큰을 추출하고 검증하여 Security Context에 인증 정보를 설정하는 필터입니다.
 * Spring Security 필터 체인에서 작동하며, 모든 요청에 대해 한 번씩 실행됩니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtTokenValidator jwtTokenValidator;
    private final CustomUserDetailsService userDetailsService;

    /**
     * JWT 토큰을 검증하고 인증 정보를 설정하는 필터 메서드입니다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException IO 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 요청에서 JWT 토큰 추출
        String token = jwtUtil.resolveToken(request);

        // 토큰이 존재하고 현재 인증 정보가 없는 경우에만 처리
        if (StringUtils.hasText(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 토큰 유효성 검증
                if (jwtTokenValidator.validateToken(token)) {
                    String userId = jwtUtil.getSubject(token);

                    // DB에서 CustomUserDetails 조회
                    CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserById(userId);

                    //CustomUserDetails기반으로 인증 객체 생성
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("JWT 인증 성공: {}", userId);
                }
            } catch (CustomException e) {
                // 토큰 검증 실패 시 로깅
                log.error("JWT 인증 실패: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        // 다음 필터 실행
        filterChain.doFilter(request, response);
    }
}