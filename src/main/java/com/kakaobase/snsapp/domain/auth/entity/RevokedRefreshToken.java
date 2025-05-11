package com.kakaobase.snsapp.domain.auth.entity;

import com.kakaobase.snsapp.global.common.entity.BaseCreatedTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * 취소된 리프레시 토큰 정보를 저장하는 엔티티입니다.
 * 로그아웃 등으로 인해 더 이상 유효하지 않은 토큰을 관리합니다.
 */
@Entity
@Table(name = "revoked_refresh_tokens")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class RevokedRefreshToken extends BaseCreatedTimeEntity {

    /**
     * 취소된, SHA-256으로 해시된 리프레시 토큰 값입니다.
     * 기본 키로 사용됩니다.
     */
    @Id
    @Column(name = "revoked_refresh_token_hash", nullable = false, length = 43)
    private String refreshTokenHash;

    /**
     * 리프레시 토큰을 발급받은 회원의 ID입니다.
     */
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    /**
     * 리프레시 토큰이 취소된 시간입니다.
     * BaseCreatedTimeEntity의 createdAt 필드가 이 역할을 합니다.
     * revoked_at 컬럼에 매핑됩니다.
     */
    @CreatedDate
    @Column(name = "revoked_at", insertable = false, updatable = false)
    private LocalDateTime revokedAt;

    /**
     * 토큰이 특정 회원의 것인지 확인합니다.
     *
     * @param memberId 확인할 회원 ID
     * @return 토큰이 해당 회원의 것이면 true, 아니면 false
     */
    public boolean isTokenForMember(Long memberId) {
        return this.memberId.equals(memberId);
    }

    @Builder
    RevokedRefreshToken(String refreshTokenHash, Long memberId) {
        this.refreshTokenHash = refreshTokenHash;
        this.memberId = memberId;
    }


}