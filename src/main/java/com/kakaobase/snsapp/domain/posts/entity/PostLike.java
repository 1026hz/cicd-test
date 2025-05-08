package com.kakaobase.snsapp.domain.posts.entity;

import com.kakaobase.snsapp.global.common.entity.BaseCreatedTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 좋아요 정보를 담는 엔티티
 * <p>
 * 게시글에 좋아요를 누른 회원 정보를 관리합니다.
 * BaseCreatedTimeEntity를 상속받아 생성 시간 정보를 관리합니다.
 * 복합 기본키를 사용합니다 (회원 ID + 게시글 ID).
 * </p>
 */
@Entity
@Table(
        name = "posts_likes",
        indexes = {
                @Index(name = "idx_member_id", columnList = "member_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(PostLike.PostLikeId.class)
public class PostLike extends BaseCreatedTimeEntity {

    @Id
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Id
    @Column(name = "post_id", nullable = false)
    private Long postId;

    /**
     * 좋아요 정보 생성을 위한 생성자
     *
     * @param memberId 좋아요를 누른 회원 ID
     * @param postId 좋아요가 눌린 게시글 ID
     */
    public PostLike(Long memberId, Long postId) {
        this.memberId = memberId;
        this.postId = postId;
    }

    /**
     * PostLike 엔티티의 복합 기본키 클래스
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PostLikeId implements java.io.Serializable {
        private Long memberId;
        private Long postId;

        public PostLikeId(Long memberId, Long postId) {
            this.memberId = memberId;
            this.postId = postId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PostLikeId that = (PostLikeId) o;

            if (!memberId.equals(that.memberId)) return false;
            return postId.equals(that.postId);
        }

        @Override
        public int hashCode() {
            int result = memberId.hashCode();
            result = 31 * result + postId.hashCode();
            return result;
        }
    }
}