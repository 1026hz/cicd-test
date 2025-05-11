package com.kakaobase.snsapp.domain.auth.entity;

import com.kakaobase.snsapp.global.common.entity.BaseUpdateTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

/**
 * 사용자 인증 토큰 정보를 저장하는 엔티티입니다.
 * 리프레시 토큰 및 관련 정보를 관리합니다.
 */
@Entity
@Table(name = "auth_tokens")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class AuthToken extends BaseUpdateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 토큰을 발급받은 회원의 ID입니다.
     */
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    /**
     * 리프레시 토큰의 해시값입니다.
     * SHA-256 암호화를 통해 저장됩니다.
     */
    @Column(name = "refresh_token_hash", nullable = false, length = 43)
    private String refreshTokenHash;

    /**
     * 사용자 기기의 고유 식별자입니다.
     * UUID v4 형식으로 저장됩니다.
     */
    @Column(name = "device_id", nullable = false, length = 36)
    private String deviceId;

    /**
     * 토큰이 발급된 기기의 User-Agent 정보입니다.
     */
    @Column(name = "user_agent", nullable = false, length = 512)
    private String userAgent;

    /**
     * 토큰의 만료 시간입니다.
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * 리프레시 토큰을 갱신합니다.
     *
     * @param refreshTokenHash 새 리프레시 토큰 해시
     * @param expiresAt 새 만료 시간
     */
    public void updateToken(String refreshTokenHash, LocalDateTime expiresAt) {
        this.refreshTokenHash = refreshTokenHash;
        this.expiresAt = expiresAt;
    }

    /**
     * 토큰이 만료되었는지 확인합니다.
     *
     * @return 현재 시간이 만료 시간을 지났으면 true, 그렇지 않으면 false
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * 특정 해시값이 현재 토큰의 해시값과 일치하는지 확인합니다.
     *
     * @param tokenHash 비교할 토큰 해시
     * @return 해시값이 일치하면 true, 그렇지 않으면 false
     */
    public boolean isTokenMatch(String tokenHash) {
        return this.refreshTokenHash.equals(tokenHash);
    }

    /**
     * 이 토큰이 특정 디바이스의 것인지 확인합니다.
     */
    public boolean isFromDevice(String deviceId) {
        return this.deviceId.equals(deviceId);
    }

    /**
     * 이 토큰이 특정 User-Agent로 발급되었는지 확인합니다.
     */
    public boolean isFromUserAgent(String agent) {
        return this.userAgent.equals(agent);
    }

}