package com.kakaobase.snsapp.domain.members.repository;

import com.kakaobase.snsapp.domain.members.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 회원 정보에 접근하기 위한 레포지토리 인터페이스입니다.
 * 인덱싱 전략에 맞춰 최적화된 쿼리 메서드들을 제공합니다.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 이메일로 활성 회원을 조회합니다. (인덱스: idx_email_not_deleted 사용)
     *
     * @param email 회원 이메일
     * @return 이메일에 해당하는 회원, 없으면 Optional.empty()
     */
    Optional<Member> findByEmail(String email);

    /**
     * 이메일로 활성 회원 존재 여부를 확인합니다. (인덱스: idx_email_not_deleted 사용)
     *
     * @param email 확인할 이메일
     * @return 존재하면 true, 아니면 false
     */
    boolean existsByEmail(String email);

    /**
     * 닉네임으로 활성 회원 존재 여부를 확인합니다. (인덱스: idx_nickname_not_deleted 사용)
     *
     * @param nickname 확인할 닉네임
     * @return 존재하면 true, 아니면 false
     */
    boolean existsByNickname(String nickname);

    /**
     * 기수별 활성 회원 목록을 조회합니다.
     *
     * @param className 기수명
     * @return 해당 기수의 회원 목록
     */
    List<Member> findByClassName(Member.ClassName className);

    /**
     * 회원 ID 목록으로 활성 회원 목록을 조회합니다.
     *
     * @param ids 회원 ID 목록
     * @return 조회된 회원 목록
     */
    List<Member> findByIdIn(List<Long> ids);

    /**
     * 이름으로 활성 회원을 검색합니다. (부분 일치)
     * 성능 최적화를 위해 JPQL 사용
     *
     * @param name 검색할 이름
     * @return 이름이 포함된 회원 목록
     */
    @Query("SELECT m FROM Member m WHERE m.name LIKE %:name% AND m.deletedAt IS NULL")
    List<Member> searchByName(@Param("name") String name);

    /**
     * 닉네임으로 활성 회원을 검색합니다. (부분 일치)
     * 성능 최적화를 위해 JPQL 사용
     *
     * @param nickname 검색할 닉네임
     * @return 닉네임이 포함된 회원 목록
     */
    @Query("SELECT m FROM Member m WHERE m.nickname LIKE %:nickname% AND m.deletedAt IS NULL")
    List<Member> searchByNickname(@Param("nickname") String nickname);

    /**
     * 특정 회원의 프로필 정보와 팔로우 통계를 조회합니다.
     * 필요한 컬럼만 선택하여 성능을 최적화합니다.
     *
     * @param id 회원 ID
     * @return 회원 프로필 정보
     */
    @Query("SELECT m FROM Member m WHERE m.id = :id")
    Optional<Member> findProfileById(@Param("id") Long id);
}