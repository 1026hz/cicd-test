package com.kakaobase.snsapp.domain.comments.repository;

import com.kakaobase.snsapp.domain.comments.entity.Recomment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 대댓글 엔티티에 대한 데이터 액세스 객체
 *
 * <p>대댓글에 대한 CRUD 및 다양한 조회 작업을 처리합니다.</p>
 */
@Repository
public interface RecommentRepository extends JpaRepository<Recomment, Long> {

    /**
     * 특정 댓글의 대댓글을 생성 시간 순으로 조회합니다.
     * 삭제되지 않은 대댓글만 조회합니다.
     *
     * @param commentId 댓글 ID
     * @param pageable 페이지네이션 정보
     * @return 대댓글 목록 (페이지네이션 적용)
     */
    @Query("SELECT r FROM Recomment r WHERE r.comment.id = :commentId AND r.deletedAt IS NULL ORDER BY r.createdAt ASC")
    Page<Recomment> findByCommentId(@Param("commentId") Long commentId, Pageable pageable);

    /**
     * 특정 댓글의 대댓글을 커서 기반으로 조회합니다.
     * 삭제되지 않은 대댓글만 조회합니다.
     *
     * @param commentId 댓글 ID
     * @param cursor 마지막으로 조회한 대댓글 ID (첫 페이지에서는 null 또는 0)
     * @param limit 조회할 대댓글 수
     * @return 대댓글 목록
     */
    @Query(value = "SELECT r.* FROM recomments r " +
            "WHERE r.comment_id = :commentId " +
            "AND r.deleted_at IS NULL " +
            "AND (:cursor IS NULL OR r.id < :cursor) " +
            "ORDER BY r.id DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Recomment> findByCommentIdWithCursor(
            @Param("commentId") Long commentId,
            @Param("cursor") Long cursor,
            @Param("limit") int limit);

    /**
     * 특정 회원이 작성한 대댓글을 생성 시간 역순으로 조회합니다.
     * 삭제되지 않은 대댓글만 조회합니다.
     *
     * @param memberId 회원 ID
     * @param pageable 페이지네이션 정보
     * @return 대댓글 목록 (페이지네이션 적용)
     */
    @Query("SELECT r FROM Recomment r WHERE r.member.id = :memberId AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    Page<Recomment> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    /**
     * 특정 회원이 작성한 대댓글을 커서 기반으로 조회합니다.
     * 삭제되지 않은 대댓글만 조회합니다.
     *
     * @param memberId 회원 ID
     * @param cursor 마지막으로 조회한 대댓글 ID (첫 페이지에서는 null 또는 0)
     * @param limit 조회할 대댓글 수
     * @return 대댓글 목록
     */
    @Query(value = "SELECT r.* FROM recomments r " +
            "WHERE r.member_id = :memberId " +
            "AND r.deleted_at IS NULL " +
            "AND (:cursor IS NULL OR r.id < :cursor) " +
            "ORDER BY r.id DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Recomment> findByMemberIdWithCursor(
            @Param("memberId") Long memberId,
            @Param("cursor") Long cursor,
            @Param("limit") int limit);

    /**
     * 특정 대댓글을 ID로 조회합니다. 삭제된 대댓글은 포함하지 않습니다.
     *
     * @param id 대댓글 ID
     * @return 대댓글 (Optional)
     */
    @Query("SELECT r FROM Recomment r WHERE r.id = :id AND r.deletedAt IS NULL")
    Optional<Recomment> findByIdAndDeletedAtIsNull(@Param("id") Long id);

    /**
     * 특정 댓글의 대댓글 수를 조회합니다.
     * 삭제되지 않은 대댓글만 계산합니다.
     *
     * @param commentId 댓글 ID
     * @return 대댓글 수
     */
    @Query("SELECT COUNT(r) FROM Recomment r WHERE r.comment.id = :commentId AND r.deletedAt IS NULL")
    long countByCommentIdAndDeletedAtIsNull(@Param("commentId") Long commentId);

    /**
     * 특정 댓글의 모든 대댓글을 조회합니다.
     * 삭제된 대댓글도 포함됩니다.
     *
     * @param commentId 댓글 ID
     * @return 대댓글 목록
     */
    List<Recomment> findByCommentId(Long commentId);

    /**
     * 특정 댓글에 달린 대댓글 중 최근 몇 개를 조회합니다.
     * 삭제되지 않은 대댓글만 조회하며, 최신순으로 정렬합니다.
     *
     * @param commentId 댓글 ID
     * @param limit 조회할 대댓글 수
     * @return 대댓글 목록
     */
    @Query(value = "SELECT r.* FROM recomments r " +
            "WHERE r.comment_id = :commentId " +
            "AND r.deleted_at IS NULL " +
            "ORDER BY r.created_at DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Recomment> findRecentByCommentId(
            @Param("commentId") Long commentId,
            @Param("limit") int limit);
}