package com.kakaobase.snsapp.domain.posts.repository;

import com.kakaobase.snsapp.domain.posts.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 게시글 이미지 엔티티에 대한 데이터 액세스 객체
 *
 * <p>게시글 이미지에 대한 CRUD 및 다양한 조회 작업을 처리합니다.</p>
 */
@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    /**
     * 특정 게시글의 이미지를 순서대로 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 게시글에 첨부된 이미지 목록 (순서대로 정렬됨)
     */
    List<PostImage> findByPostIdOrderBySortIndexAsc(Long postId);

    /**
     * 특정 게시글의 이미지를 ID로 조회합니다.
     *
     * @param id 이미지 ID
     * @param postId 게시글 ID
     * @return 조건에 맞는 이미지 (Optional)
     */
    Optional<PostImage> findByIdAndPostId(Long id, Long postId);

    /**
     * 특정 게시글의 특정 순서에 있는 이미지를 조회합니다.
     *
     * @param postId 게시글 ID
     * @param sortIndex 이미지 순서
     * @return 조건에 맞는 이미지 (Optional)
     */
    Optional<PostImage> findByPostIdAndSortIndex(Long postId, Integer sortIndex);

    /**
     * 특정 게시글의 이미지 중 지정된 이미지 URL을 가진 이미지를 찾습니다.
     *
     * @param postId 게시글 ID
     * @param imgUrl 이미지 URL
     * @return 조건에 맞는 이미지 목록
     */
    List<PostImage> findByPostIdAndImgUrl(Long postId, String imgUrl);

    /**
     * 특정 게시글의 모든 이미지를 삭제합니다.
     *
     * @param postId 게시글 ID
     */
    @Modifying
    @Query("DELETE FROM PostImage pi WHERE pi.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    /**
     * 특정 게시글의 이미지 수를 조회합니다.
     *
     * @param postId 게시글 ID
     * @return 이미지 수
     */
    long countByPostId(Long postId);

    /**
     * 특정 게시글의 최대 이미지 정렬 인덱스를 조회합니다.
     * 새 이미지 추가 시 다음 인덱스 결정에 사용됩니다.
     *
     * @param postId 게시글 ID
     * @return 최대 정렬 인덱스 (결과가 없으면 null)
     */
    @Query("SELECT MAX(pi.sortIndex) FROM PostImage pi WHERE pi.post.id = :postId")
    Integer findMaxSortIndexByPostId(@Param("postId") Long postId);

    /**
     * 특정 게시글의 이미지 순서를 업데이트합니다.
     *
     * @param id 이미지 ID
     * @param sortIndex 새 정렬 인덱스
     */
    @Modifying
    @Query("UPDATE PostImage pi SET pi.sortIndex = :sortIndex WHERE pi.id = :id")
    void updateSortIndex(@Param("id") Long id, @Param("sortIndex") Integer sortIndex);

    /**
     * 특정 이미지 URL을 사용하는 모든 이미지를 찾습니다.
     * S3에서 이미지 삭제 시 참조 확인에 사용됩니다.
     *
     * @param imgUrl 이미지 URL
     * @return 해당 URL을 사용하는 이미지 목록
     */
    List<PostImage> findByImgUrl(String imgUrl);
}