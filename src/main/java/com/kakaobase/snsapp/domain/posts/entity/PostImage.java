package com.kakaobase.snsapp.domain.posts.entity;

import com.kakaobase.snsapp.global.common.entity.BaseUpdateTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 이미지 정보를 담는 엔티티
 * <p>
 * 게시글에 첨부된 이미지의 URL과 순서 정보를 관리합니다.
 * BaseUpdateTimeEntity를 상속받아 생성 시간, 수정 시간 정보를 관리합니다.
 * </p>
 */
@Entity
@Table(
        name = "post_imgs",
        indexes = {
                @Index(name = "idx_post_sort", columnList = "post_id, sort_index")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage extends BaseUpdateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "sort_index", nullable = false)
    private Integer sortIndex;

    @Column(name = "img_url", nullable = false, length = 512)
    private String imgUrl;

    /**
     * 이미지 정보 생성을 위한 생성자
     *
     * @param post 연결된 게시글
     * @param sortIndex 이미지 정렬 순서
     * @param imgUrl 이미지 URL
     */
    @Builder
    public PostImage(Post post, Integer sortIndex, String imgUrl) {
        this.post = post;
        this.sortIndex = sortIndex;
        this.imgUrl = imgUrl;
    }
}