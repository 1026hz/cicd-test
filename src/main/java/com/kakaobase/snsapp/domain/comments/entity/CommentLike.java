package com.kakaobase.snsapp.domain.comments.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 댓글 좋아요 정보를 담는 엔티티
 * <p>
 * 댓글에 좋아요를 누른 회원 정보를 관리합니다.
 * 복합 기본키를 사용합니다 (회원 ID + 댓글 ID).
 * </p>
 */
@Entity
@Table(
        name = "comment_likes",
        indexes = {
                @Index(name = "idx_member_id", columnList = "member_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(CommentLike.CommentLikeId.class)
public class CommentLike {

    @Id
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Id
    @Column(name = "comment_id", nullable = false)
    private Long commentId;

    /**
     * 좋아요 정보 생성을 위한 생성자
     *
     * @param memberId 좋아요를 누른 회원 ID
     * @param commentId 좋아요가 눌린 댓글 ID
     */
    public CommentLike(Long memberId, Long commentId) {
        this.memberId = memberId;
        this.commentId = commentId;
    }

    /**
     * CommentLike 엔티티의 복합 기본키 클래스
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CommentLikeId implements java.io.Serializable {
        private Long memberId;
        private Long commentId;

        public CommentLikeId(Long memberId, Long commentId) {
            this.memberId = memberId;
            this.commentId = commentId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CommentLikeId that = (CommentLikeId) o;

            if (!memberId.equals(that.memberId)) return false;
            return commentId.equals(that.commentId);
        }

        @Override
        public int hashCode() {
            int result = memberId.hashCode();
            result = 31 * result + commentId.hashCode();
            return result;
        }
    }
}