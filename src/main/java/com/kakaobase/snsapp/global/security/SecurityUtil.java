package com.kakaobase.snsapp.global.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
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
     * 현재 인증 정보를 Optional로 가져옵니다.
     *
     * @return 인증 정보를 담은 Optional
     */
    public static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * 현재 인증된 사용자의 멤버 ID를 String으로 반환합니다.
     * 인증 정보가 없는 경우 빈 Optional을 반환합니다.
     *
     * @return 멤버 ID를 담은 Optional<String>
     */
    public static Optional<String> getMemberIdAsString() {
        Optional<Authentication> auth = getAuthentication();
        log.debug("인증 객체: {}", auth.orElse(null));

        if (auth.isPresent()) {
            Object principal = auth.get().getPrincipal();
            log.debug("인증 객체의 Principal 타입: {}", principal != null ? principal.getClass().getName() : "null");
            log.debug("인증 객체의 Principal 내용: {}", principal);
            log.debug("인증 객체의 Name: {}", auth.get().getName());
        }

        return auth
                .filter(a -> a.getPrincipal() != null && !"anonymousUser".equals(a.getPrincipal()))
                .map(Authentication::getName);
    }

    /**
     * 현재 인증된 사용자의 멤버 ID를 Long으로 반환합니다.
     * 인증 정보가 없거나 ID를 Long으로 변환할 수 없는 경우 빈 Optional을 반환합니다.
     *
     * @return 멤버 ID를 담은 Optional<Long>
     */
    public static Optional<Long> getMemberIdAsLong() {
        return getMemberIdAsString().flatMap(id -> {
            try {
                return Optional.of(Long.parseLong(id));
            } catch (NumberFormatException e) {
                log.warn("멤버 ID를 Long으로 변환할 수 없습니다: {}", id);
                return Optional.empty();
            }
        });
    }

    /**
     * 현재 인증된 사용자의 권한 목록을 가져옵니다.
     *
     * @return 권한 문자열 집합을 담은 Optional
     */
    public static Optional<Set<String>> getMemberRoles() {
        return getAuthentication()
                .map(Authentication::getAuthorities)
                .map(authorities -> authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));
    }

    /**
     * 현재 인증된 사용자의 주요 권한을 반환합니다.
     * 인증 정보나 권한 정보가 없는 경우 빈 Optional을 반환합니다.
     *
     * @return 사용자 권한을 담은 Optional
     */
    public static Optional<String> getMemberRole() {
        return getMemberRoles()
                .filter(roles -> !roles.isEmpty())
                .flatMap(roles -> roles.stream()
                        .findFirst()
                        .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role));
    }

    /**
     * 현재 인증된 사용자의 기수(class_name)를 반환합니다.
     *
     * @return 사용자 기수를 담은 Optional, 인증되지 않았거나 정보가 없으면 빈 Optional
     */
    public static Optional<String> getMemberClassName() {
        return getAuthentication()
                .map(Authentication::getDetails)
                .filter(details -> details instanceof Map)
                .map(details -> {
                    @SuppressWarnings("unchecked")
                    Map<String, String> detailsMap = (Map<String, String>) details;
                    return detailsMap.get("class_name");
                });
    }

    /**
     * 현재 사용자가 인증되었는지 확인합니다.
     *
     * @return 인증되었으면 true, 그렇지 않으면 false
     */
    public static boolean isAuthenticated() {
        return getAuthentication()
                .filter(auth -> auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                .isPresent();
    }

    /**
     * 현재 사용자가 특정 멤버 ID와 일치하는지 확인합니다.
     *
     * @param memberId 확인할 멤버 ID
     * @return 일치하면 true, 그렇지 않으면 false
     */
    public static boolean isCurrentMember(String memberId) {
        if (memberId == null) {
            return false;
        }

        return getMemberIdAsString()
                .map(currentMemberId -> currentMemberId.equals(memberId))
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

        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return getMemberRoles()
                .map(roles -> roles.stream()
                        .anyMatch(r -> r.equals(role) || r.equals(roleWithPrefix)))
                .orElse(false);
    }

    /**
     * 현재 사용자가 관리자 또는 봇 역할을 가지고 있는지 확인합니다.
     *
     * @return 관리자 또는 봇 권한이 있으면 true, 아니면 false
     */
    public static boolean isAdminOrBot() {
        return hasRole("ADMIN") || hasRole("BOT") ||
                hasRole("ROLE_ADMIN") || hasRole("ROLE_BOT");
    }
}