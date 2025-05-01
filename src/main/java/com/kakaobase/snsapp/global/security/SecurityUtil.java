package com.kakaobase.snsapp.global.security;

import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import com.kakaobase.snsapp.global.error.exception.CustomException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 현재 인증된 사용자 정보를 조회하는 유틸리티 클래스입니다.
 * SecurityContextHolder를 통해 인증 객체에 접근합니다.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {

    /**
     * 현재 인증된 사용자의 ID를 반환합니다.
     *
     * @return 사용자 ID
     * @throws CustomException 인증된 사용자가 없는 경우 발생
     */
    public static String getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new CustomException(GeneralErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        return authentication.getName();
    }

    /**
     * 현재 인증된 사용자의 역할을 반환합니다.
     *
     * @return 사용자 역할
     * @throws CustomException 인증된 사용자가 없는 경우 발생
     */
    public static String getCurrentUserRole() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getAuthorities() == null) {
            throw new CustomException(GeneralErrorCode.UNAUTHORIZED, "인증 정보가 없습니다.");
        }

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // "ROLE_" 접두사 제거
        return roles.stream()
                .findFirst()
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                .orElseThrow(() -> new CustomException(GeneralErrorCode.UNAUTHORIZED, "역할 정보가 없습니다."));
    }

    /**
     * 현재 사용자가 인증되었는지 확인합니다.
     *
     * @return 인증되었으면 true, 그렇지 않으면 false
     */
    public static boolean isAuthenticated() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * 현재 사용자가 특정 사용자 ID와 일치하는지 확인합니다.
     *
     * @param userId 확인할 사용자 ID
     * @return 일치하면 true, 그렇지 않으면 false
     */
    public static boolean isCurrentUser(String userId) {
        try {
            String currentUserId = getCurrentUserId();
            return currentUserId.equals(userId);
        } catch (CustomException e) {
            return false;
        }
    }

    /**
     * 현재 사용자가 특정 역할을 가지고 있는지 확인합니다.
     *
     * @param role 확인할 역할
     * @return 역할을 가지고 있으면 true, 그렇지 않으면 false
     */
    public static boolean hasRole(String role) {
        try {
            String currentRole = getCurrentUserRole();
            return currentRole.equals(role);
        } catch (CustomException e) {
            return false;
        }
    }
}