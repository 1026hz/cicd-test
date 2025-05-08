package com.kakaobase.snsapp.domain.posts.repository;

import com.kakaobase.snsapp.domain.posts.entity.PostLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 게시글 좋아요 엔티티에 대한 데이터 액세스 객체
 *
 * <p>게시글 좋아요에 대한 CRUD 및 다양한 조회 작업을 처리합니다.</p>
 */
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, PostLike.PostLikeId> {

    /**
     * 특정 회원이 특정 게시글에 좋아요를 눌렀는지 확인합니다.
     *
     * @param memberId 회원 ID
     * @param postId 게시글 ID
     * @return 좋아요 정보 (Optional)
     */
    Optional<PostLike> findByMemberIdAndPostId(Long memberId, Long postId);

    /**
     * 특정 회원이 특정 게시글에 좋아요를 눌렀는지 여부를 확인합니다.
     *
     * @param memberId 회원 ID
     * @param postId 게시글 ID
     * @return 좋아요를 눌렀으면 true, 아니면 false
     */
    boolean existsByMemberIdAndPostId(Long memberId, Long postId);

    /**
     * 특정 회원이 좋아요를 누른 게시글 ID 목록을 조회합니다.
     *
     * @param memberId 회원 ID
     * @return 좋아요를 누른 게시글 ID 목록
     */
    @Query("SELECT pl.postId FROM PostLike pl WHERE pl.memberId = :memberId")
    List<Long> findPostIdsByMemberId(@Param("memberId") Long memberId);

    /**
     * 특정 회원이 주어진 게시글 목록 중 좋아요를 누른 게시글 ID 목록을 조회합니다.
     * 게시글 목록 조회 시 좋아요 여부를 확인하는 데 사용됩니다.
     *
     * @param memberId 회원 ID
     * @param posts 게시글 목록
     * @return 좋아요를 누른 게시글 ID 목록
     */
    @Query("SELECT pl.postId FROM PostLike pl WHERE pl.memberId = :memberId AND pl.postId IN :postIds")
    List<Long> findPostIdsByMemberIdAndPostIdIn(
            @Param("memberId") Long memberId,
            @Param("postIds") List<Long> postIds);

    /**
     * 특정 게시글의 좋아요 수를 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 좋아요 수
     */
    long countByPostId(Long postId);

    /**
     * 특정 회원이 좋아요를 누른 게시글 목록을 페이지네이션하여 조회합니다.
     *
     * @param memberId 회원 ID
     * @param pageable 페이지네이션 정보
     * @return 좋아요를 누른 게시글 ID 목록 (페이지네이션 적용)
     */
    Page<PostLike> findByMemberId(Long memberId, Pageable pageable);

    /**
     * 특정 게시글의 모든 좋아요를 삭제합니다.
     * 게시글 삭제 시 관련 좋아요도 함께 삭제하는 데 사용됩니다.
     *
     * @param postId 게시글 ID
     */
    void deleteByPostId(Long postId);

    /**
     * 특정 회원의 모든 좋아요를 삭제합니다.
     * 회원 탈퇴 시 관련 좋아요도 함께 삭제하는 데 사용됩니다.
     *
     * @param memberId 회원 ID
     */
    void deleteByMemberId(Long memberId);
    /**
     * 특정 게시글에 좋아요를 누른 상위 N명의 회원 ID를 조회합니다.
     * 게시글에 좋아요를 누른 사용자 목록을 표시할 때 사용됩니다.
     *
     * @param postId 게시글 ID
     * @param limit 최대 조회할 회원 수
     * @return 좋아요를 누른 회원 ID 목록 (최대 limit명)
     */
    @Query(value = "SELECT pl.member_id FROM posts_likes pl WHERE pl.post_id = :postId ORDER BY pl.created_at DESC LIMIT :limit", nativeQuery = true)
    List<Long> findTopNMemberIdsByPostId(@Param("postId") Long postId, @Param("limit") int limit);
}