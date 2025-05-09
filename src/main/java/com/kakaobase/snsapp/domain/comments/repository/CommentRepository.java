package com.kakaobase.snsapp.domain.comments.repository;

import com.kakaobase.snsapp.domain.comments.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 댓글 엔티티에 대한 데이터 액세스 객체
 *
 * <p>댓글에 대한 CRUD 및 다양한 조회 작업을 처리합니다.</p>
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 특정 게시글의 최상위 댓글(대댓글이 아닌 댓글)을 생성 시간 역순으로 조회합니다.
     * 삭제되지 않은 댓글만 조회합니다.
     *
     * @param postId 게시글 ID
     * @param pageable 페이지네이션 정보
     * @return 댓글 목록 (페이지네이션 적용)
     */
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.parentComment IS NULL AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    Page<Comment> findTopLevelCommentsByPostId(@Param("postId") Long postId, Pageable pageable);

    /**
     * 특정 게시글의 삭제되지 않은 댓글을 커서 기반으로 조회합니다.
     * 최상위 댓글(대댓글이 아닌 댓글)만 조회합니다.
     *
     * @param postId 게시글 ID
     * @param cursor 마지막으로 조회한 댓글 ID (첫 페이지에서는 null 또는 0)
     * @param limit 조회할 댓글 수
     * @return 댓글 목록
     */
    @Query(value = "SELECT c.* FROM comments c " +
            "WHERE c.post_id = :postId " +
            "AND c.parent_id IS NULL " +
            "AND c.deleted_at IS NULL " +
            "AND (:cursor IS NULL OR c.id < :cursor) " +
            "ORDER BY c.id DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Comment> findTopLevelCommentsByPostIdWithCursor(
            @Param("postId") Long postId,
            @Param("cursor") Long cursor,
            @Param("limit") int limit);

    /**
     * 특정 댓글의 대댓글을 생성 시간 순으로 조회합니다.
     * 삭제되지 않은 대댓글만 조회합니다.
     *
     * @param commentId 댓글 ID
     * @param pageable 페이지네이션 정보
     * @return 대댓글 목록 (페이지네이션 적용)
     */
    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :commentId AND c.deletedAt IS NULL ORDER BY c.createdAt ASC")
    Page<Comment> findRepliesByCommentId(@Param("commentId") Long commentId, Pageable pageable);

    /**
     * 특정 댓글의 대댓글을 커서 기반으로 조회합니다.
     * 삭제되지 않은 대댓글만 조회합니다.
     *
     * @param commentId 댓글 ID
     * @param cursor 마지막으로 조회한 대댓글 ID (첫 페이지에서는 null 또는 0)
     * @param limit 조회할 대댓글 수
     * @return 대댓글 목록
     */
    @Query(value = "SELECT c.* FROM comments c " +
            "WHERE c.parent_id = :commentId " +
            "AND c.deleted_at IS NULL " +
            "AND (:cursor IS NULL OR c.id < :cursor) " +
            "ORDER BY c.id DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Comment> findRepliesByCommentIdWithCursor(
            @Param("commentId") Long commentId,
            @Param("cursor") Long cursor,
            @Param("limit") int limit);

    /**
     * 특정 회원이 작성한 댓글을 생성 시간 역순으로 조회합니다.
     * 삭제되지 않은 댓글만 조회합니다.
     *
     * @param memberId 회원 ID
     * @param pageable 페이지네이션 정보
     * @return 댓글 목록 (페이지네이션 적용)
     */
    @Query("SELECT c FROM Comment c WHERE c.member.id = :memberId AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    Page<Comment> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    /**
     * 특정 회원이 작성한 댓글을 커서 기반으로 조회합니다.
     * 삭제되지 않은 댓글만 조회합니다.
     *
     * @param memberId 회원 ID
     * @param cursor 마지막으로 조회한 댓글 ID (첫 페이지에서는 null 또는 0)
     * @param limit 조회할 댓글 수
     * @return 댓글 목록
     */
    @Query(value = "SELECT c.* FROM comments c " +
            "WHERE c.member_id = :memberId " +
            "AND c.deleted_at IS NULL " +
            "AND (:cursor IS NULL OR c.id < :cursor) " +
            "ORDER BY c.id DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Comment> findByMemberIdWithCursor(
            @Param("memberId") Long memberId,
            @Param("cursor") Long cursor,
            @Param("limit") int limit);

    /**
     * 특정 댓글을 ID로 조회합니다. 삭제된 댓글은 포함하지 않습니다.
     *
     * @param id 댓글 ID
     * @return 댓글 (Optional)
     */
    @Query("SELECT c FROM Comment c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Comment> findByIdAndDeletedAtIsNull(@Param("id") Long id);

    /**
     * 특정 게시글의 댓글 수를 조회합니다.
     * 삭제되지 않은 댓글만 계산합니다.
     *
     * @param postId 게시글 ID
     * @return 댓글 수
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId AND c.deletedAt IS NULL")
    long countByPostIdAndDeletedAtIsNull(@Param("postId") Long postId);

    /**
     * 특정 게시글의 댓글을 페이지네이션하여 조회합니다.
     * 삭제되지 않은 댓글만 조회합니다.
     *
     * @param postId 게시글 ID
     * @param pageable 페이지네이션 정보
     * @return 댓글 목록 (페이지네이션 적용)
     */
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.deletedAt IS NULL")
    Page<Comment> findByPostId(@Param("postId") Long postId, Pageable pageable);

    /**
     * 특정 게시글의 댓글을 커서 기반으로 조회합니다.
     * ID가 특정 값보다 작은 댓글들을 ID 내림차순으로 조회합니다.
     * 삭제되지 않은 댓글만 조회합니다.
     *
     * @param postId 게시글 ID
     * @param commentId 커서(특정 댓글 ID)
     * @param limit 조회할 최대 댓글 수
     * @return 댓글 목록
     */
    @Query(value = "SELECT c.* FROM comments c " +
            "WHERE c.post_id = :postId " +
            "AND c.id < :commentId " +
            "AND c.deleted_at IS NULL " +
            "ORDER BY c.id DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Comment> findByPostIdAndIdLessThanOrderByIdDesc(
            @Param("postId") Long postId,
            @Param("commentId") Long commentId,
            @Param("limit") int limit);
}