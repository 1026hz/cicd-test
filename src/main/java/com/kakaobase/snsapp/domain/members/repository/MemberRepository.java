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
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 이메일로 회원을 조회합니다.
     *
     * @param email 회원 이메일
     * @return 이메일에 해당하는 회원, 없으면 Optional.empty()
     */
    Optional<Member> findByEmail(String email);

    /**
     * 닉네임으로 회원을 조회합니다.
     *
     * @param nickname 회원 닉네임
     * @return 닉네임에 해당하는 회원, 없으면 Optional.empty()
     */
    Optional<Member> findByNickname(String nickname);

    /**
     * 이메일 존재 여부를 확인합니다.
     *
     * @param email 확인할 이메일
     * @return 존재하면 true, 아니면 false
     */
    boolean existsByEmail(String email);

    /**
     * 닉네임 존재 여부를 확인합니다.
     *
     * @param nickname 확인할 닉네임
     * @return 존재하면 true, 아니면 false
     */
    boolean existsByNickname(String nickname);

    /**
     * 기수별 회원 목록을 조회합니다.
     *
     * @param className 기수명
     * @return 해당 기수의 회원 목록
     */
    List<Member> findByClassName(Member.ClassName className);

    /**
     * 이름으로 회원을 검색합니다. (부분 일치)
     *
     * @param name 검색할 이름
     * @return 이름이 포함된 회원 목록
     */
    List<Member> findByNameContaining(String name);

    /**
     * 닉네임으로 회원을 검색합니다. (부분 일치)
     *
     * @param nickname 검색할 닉네임
     * @return 닉네임이 포함된 회원 목록
     */
    List<Member> findByNicknameContaining(String nickname);

    /**
     * 회원 ID 목록으로 회원 목록을 조회합니다.
     *
     * @param ids 회원 ID 목록
     * @return 조회된 회원 목록
     */
    List<Member> findByIdIn(List<Long> ids);

    /**
     * 팔로워 수가 많은 순으로 회원을 조회합니다.
     *
     * @param limit 조회할 회원 수
     * @return 팔로워 수 순으로 정렬된 회원 목록
     */
    @Query("SELECT m FROM Member m ORDER BY m.followerCount DESC")
    List<Member> findTopByFollowerCount(int limit);

    /**
     * 특정 기수의 회원 수를 조회합니다.
     *
     * @param className 기수명
     * @return 해당 기수의 회원 수
     */
    long countByClassName(Member.ClassName className);

    /**
     * 특정 회원의 프로필 정보를 부분 조회합니다. (성능 최적화)
     *
     * @param id 회원 ID
     * @return 회원 엔티티 (선택된 필드만 포함)
     */
    @Query("SELECT new Member(m.id, m.email, m.name, m.nickname, m.className, m.profileImgUrl, m.githubUrl, m.followerCount, m.followingCount) " +
            "FROM Member m WHERE m.id = :id")
    Optional<Member> findProfileById(@Param("id") Long id);
}