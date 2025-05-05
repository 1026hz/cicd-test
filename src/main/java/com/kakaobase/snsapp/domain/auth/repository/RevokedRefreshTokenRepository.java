package com.kakaobase.snsapp.domain.auth.repository;

import com.kakaobase.snsapp.domain.auth.entity.RevokedRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 취소된 리프레시 토큰 정보에 접근하기 위한 레포지토리 인터페이스입니다.
 * 로그아웃 된 토큰이나 보안상 무효화된 토큰 관리를 위한 메서드들을 제공합니다.
 */
@Repository
public interface RevokedRefreshTokenRepository extends JpaRepository<RevokedRefreshToken, String> {

    /**
     * 특정 리프레시 토큰 해시가 취소 목록에 존재하는지 확인합니다.
     * 토큰 유효성 검증 과정에서 사용됩니다.
     *
     * @param refreshTokenHash 리프레시 토큰 해시값
     * @return 취소 목록에 존재하면 true, 아니면 false
     */
    boolean existsByRefreshTokenHash(String refreshTokenHash);

    /**
     * 특정 회원 ID와 관련된 모든 취소된 리프레시 토큰을 조회합니다.
     * 회원별 토큰 관리에 사용됩니다.
     *
     * @param memberId 회원 ID
     * @return 해당 회원의 취소된 토큰 목록
     */
    List<RevokedRefreshToken> findByMemberId(Long memberId);

    /**
     * 특정 회원 ID와 관련된 모든 취소된 리프레시 토큰을 삭제합니다.
     * 회원 탈퇴 시 관련 데이터 정리에 사용될 수 있습니다.
     *
     * @param memberId 회원 ID
     * @return 삭제된 행 수
     */
    int deleteByMemberId(Long memberId);

    /**
     * 특정 시간 이전에 취소된 토큰들을 삭제합니다.
     * 데이터베이스 정리 작업에 사용됩니다.
     *
     * @param revokedBefore 기준 취소 시간
     * @return 삭제된 행 수
     */
    @Query("DELETE FROM RevokedRefreshToken r WHERE r.revokedAt < :revokedBefore")
    int deleteByRevokedAtBefore(@Param("revokedBefore") LocalDateTime revokedBefore);

}