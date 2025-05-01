package com.kakaobase.snsapp.global.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
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
     * 현재 인증된 사용자의 ID를 Optional로 반환합니다.
     * 인증 정보가 없는 경우 빈 Optional을 반환합니다.
     *
     * @return 사용자 ID를 담은 Optional
     */
    public static Optional<String> getCurrentUserIdOpt() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        return Optional.ofNullable(authentication.getName());
    }

    /**
     * 현재 인증된 사용자의 ID를 반환합니다.
     * 인증 정보가 없는 경우 null을 반환합니다.
     *
     * @return 사용자 ID, 인증되지 않은 경우 null
     */
    public static String getCurrentUserId() {
        return getCurrentUserIdOpt().orElse(null);
    }

    /**
     * 현재 인증된 사용자의 역할을 Optional로 반환합니다.
     * 인증 정보나 역할 정보가 없는 경우 빈 Optional을 반환합니다.
     *
     * @return 사용자 역할을 담은 Optional
     */
    public static Optional<String> getCurrentUserRoleOpt() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getAuthorities() == null) {
            return Optional.empty();
        }

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // "ROLE_" 접두사 제거
        return roles.stream()
                .findFirst()
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role);
    }

    /**
     * 현재 인증된 사용자의 역할을 반환합니다.
     * 인증 정보나 역할 정보가 없는 경우 null을 반환합니다.
     *
     * @return 사용자 역할, 인증되지 않은 경우 null
     */
    public static String getCurrentUserRole() {
        return getCurrentUserRoleOpt().orElse(null);
    }

    /**
     * 현재 인증된 사용자의 기수(class_name)를 반환합니다.
     *
     * @return 사용자 기수, 인증되지 않았거나 정보가 없으면 null
     */
    public static String getCurrentUserClassName() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getDetails() == null) {
            return null;
        }

        if (authentication.getDetails() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> details = (Map<String, String>) authentication.getDetails();
            return details.get("class_name");
        }


        return null;
    }

    /**
     * 현재 사용자가 인증되었는지 확인합니다.
     *
     * @return 인증되었으면 true, 그렇지 않으면 false
     */
    public static boolean isAuthenticated() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * 현재 사용자가 특정 사용자 ID와 일치하는지 확인합니다.
     *
     * @param userId 확인할 사용자 ID
     * @return 일치하면 true, 그렇지 않으면 false
     */
    public static boolean isCurrentUser(String userId) {
        if (userId == null) {
            return false;
        }

        return getCurrentUserIdOpt()
                .map(currentUserId -> currentUserId.equals(userId))
                .orElse(false);
    }

    /**
     * 현재 사용자가 특정 역할을 가지고 있는지 확인합니다.
     *
     * @param role 확인할 역할
     * @return 역할을 가지고 있으면 true, 그렇지 않으면 false
     */
    public static boolean hasRole(String role) {
        if (role == null) {
            return false;
        }

        return getCurrentUserRoleOpt()
                .map(currentRole -> currentRole.equals(role))
                .orElse(false);
    }
}