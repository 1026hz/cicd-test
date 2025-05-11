package com.kakaobase.snsapp.global.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 엔티티의 생성 시간을 자동으로 관리하는 기본 엔티티 클래스입니다.
 * 모든 엔티티의 기본이 되는 클래스로, 생성 시간 정보만 포함합니다.
 *
 * 생성 시간만 필요한 엔티티(예: follow, post_like, comment_like 등)에서 사용합니다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseCreatedTimeEntity {

    /**
     * 엔티티 생성 시간입니다.
     * 엔티티가 처음 저장될 때 자동으로 설정되며, 이후 변경되지 않습니다.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}