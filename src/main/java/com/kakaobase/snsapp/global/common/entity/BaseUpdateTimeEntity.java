package com.kakaobase.snsapp.global.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 엔티티의 생성 및 수정 시간을 자동으로 관리하는 기본 엔티티 클래스입니다.
 * 생성 시간과 수정 시간 정보를 모두 추적해야 하는 엔티티에서 사용합니다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseUpdateTimeEntity extends BaseCreatedTimeEntity {

    /**
     * 엔티티 최종 수정 시간입니다.
     * 엔티티가 수정될 때마다 자동으로 현재 시간으로 업데이트됩니다.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}