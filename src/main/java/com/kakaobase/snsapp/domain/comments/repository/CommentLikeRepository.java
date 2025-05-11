package com.kakaobase.snsapp.domain.comments.repository;

import com.kakaobase.snsapp.domain.comments.entity.CommentLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 댓글 좋아요 엔티티에 대한 데이터 액세스 객체
 *
 * <p>댓글 좋아요에 대한 CRUD 및 다양한 조회 작업을 처리합니다.</p>
 */
@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLike.CommentLikeId> {

    /**
     * 특정 회원이 특정 댓글에 좋아요를 눌렀는지 확인합니다.
     *
     * @param memberId 회원 ID
     * @param commentId 댓글 ID
     * @return 좋아요 정보 (Optional)
     */
    Optional<CommentLike> findByMemberIdAndCommentId(Long memberId, Long commentId);

    /**
     * 특정 회원이 특정 댓글에 좋아요를 눌렀는지 여부를 확인합니다.
     *
     * @param memberId 회원 ID
     * @param commentId 댓글 ID
     * @return 좋아요를 눌렀으면 true, 아니면 false
     */
    boolean existsByMemberIdAndCommentId(Long memberId, Long commentId);

    /**
     * 특정 회원이 좋아요를 누른 댓글 ID 목록을 조회합니다.
     *
     * @param memberId 회원 ID
     * @return 좋아요를 누른 댓글 ID 목록
     */
    @Query("SELECT cl.commentId FROM CommentLike cl WHERE cl.memberId = :memberId")
    List<Long> findCommentIdsByMemberId(@Param("memberId") Long memberId);

    /**
     * 특정 회원이 주어진 댓글 목록 중 좋아요를 누른 댓글 ID 목록을 조회합니다.
     * 댓글 목록 조회 시 좋아요 여부를 확인하는 데 사용됩니다.
     *
     * @param memberId 회원 ID
     * @param commentIds 댓글 목록
     * @return 좋아요를 누른 댓글 ID 목록
     */
    @Query("SELECT cl.commentId FROM CommentLike cl WHERE cl.memberId = :memberId AND cl.commentId IN :commentIds")
    List<Long> findCommentIdsByMemberIdAndCommentIdIn(
            @Param("memberId") Long memberId,
            @Param("commentIds") List<Long> commentIds);

    /**
     * 특정 댓글의 좋아요 수를 조회합니다.
     *
     * @param commentId 댓글 ID
     * @return 좋아요 수
     */
    long countByCommentId(Long commentId);

    /**
     * 특정 회원이 좋아요를 누른 댓글 목록을 페이지네이션하여 조회합니다.
     *
     * @param memberId 회원 ID
     * @param pageable 페이지네이션 정보
     * @return 좋아요를 누른 댓글 ID 목록 (페이지네이션 적용)
     */
    Page<CommentLike> findByMemberId(Long memberId, Pageable pageable);

    /**
     * 특정 댓글의 모든 좋아요를 삭제합니다.
     * 댓글 삭제 시 관련 좋아요도 함께 삭제하는 데 사용됩니다.
     *
     * @param commentId 댓글 ID
     * @return 삭제된 좋아요 수
     */
    int deleteByCommentId(Long commentId);

    /**
     * 특정 회원의 모든 좋아요를 삭제합니다.
     * 회원 탈퇴 시 관련 좋아요도 함께 삭제하는 데 사용됩니다.
     *
     * @param memberId 회원 ID
     */
    void deleteByMemberId(Long memberId);
}