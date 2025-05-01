package com.kakaobase.snsapp.domain.auth.principal;

import com.kakaobase.snsapp.domain.members.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security의 UserDetails 인터페이스를 구현한 사용자 인증 정보 클래스입니다.
 * 인증된 사용자의 정보와 권한을 캡슐화합니다.
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Member member;
    private final String id;
    private final String username;
    private final String role;
    private final String className;

    /**
     * Member 엔티티로부터 CustomUserDetails 객체를 생성합니다.
     *
     * @param member 회원 엔티티
     */
    public CustomUserDetails(Member member) {
        this.member = member;
        this.id = member.getId().toString();
        this.username = member.getEmail();
        this.role = member.getRole();
        this.className = member.getClassName();
    }

    /**
     * 사용자의 권한 정보를 반환합니다.
     *
     * @return 사용자 권한 컬렉션
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role));
    }

    /**
     * 사용자의 비밀번호를 반환합니다.
     *
     * @return 사용자 비밀번호
     */
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    /**
     * 사용자의 식별자를 반환합니다. 여기서는 이메일을 사용합니다.
     *
     * @return 사용자 식별자 (이메일)
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 사용자 계정이 만료되지 않았는지 확인합니다.
     *
     * @return 계정이 유효하면 true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 사용자 계정이 잠기지 않았는지 확인합니다.
     *
     * @return 계정이 잠기지 않았으면 true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 사용자 자격 증명(비밀번호)이 만료되지 않았는지 확인합니다.
     *
     * @return 자격 증명이 유효하면 true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 사용자 계정이 활성화되었는지 확인합니다.
     *
     * @return 계정이 활성화되었으면 true
     */
    @Override
    public boolean isEnabled() {
        return !member.isDeleted();
    }

    /**
     * 사용자의 ID를 반환합니다.
     *
     * @return 사용자 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 사용자의 기수(class_name)를 반환합니다.
     *
     * @return 사용자 기수
     */
    public String getClassName() {
        return className;
    }

    /**
     * 사용자의 역할을 반환합니다.
     *
     * @return 사용자 역할
     */
    public String getRole() {
        return role;
    }

    /**
     * 원본 Member 엔티티를 반환합니다.
     *
     * @return Member 엔티티
     */
    public Member getMember() {
        return member;
    }
}