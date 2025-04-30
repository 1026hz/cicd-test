package com.kakaobase.snsapp.global.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 소프트 삭제 기능을 추가한 엔티티 클래스
 * 삭제 기능이 필요한 엔티티에서 상속받아 사용합니다.
 *
 * 사용 시 @SQLDelete와 @SQLRestriction 어노테이션을 함께 사용하는 것을 권장합니다:
 * @SQLDelete(sql = "UPDATE table_name SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
 * @SQLRestriction("is_deleted = false")
 */
@MappedSuperclass
@Getter
public abstract class BaseSoftDeleteEntity extends BaseTimeEntity {

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    /**
     * 엔티티를 소프트 삭제 처리합니다.
     * 이 메서드는 @SQLDelete 어노테이션이 적용되지 않는 경우에 직접 호출하여 사용할 수 있습니다.
     */
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }
}