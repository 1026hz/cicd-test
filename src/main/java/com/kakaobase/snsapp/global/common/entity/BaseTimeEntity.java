package com.kakaobase.snsapp.global.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 엔티티의 기본 시간 정보를 관리하는 추상 클래스입니다.
 * Spring Data JPA의 Auditing 기능을 활용하여 생성 시간과 수정 시간을 자동으로 관리합니다.
 *
 * <p>이 클래스를 상속받는 엔티티는 별도의 코드 없이 생성 시간과 수정 시간이 자동으로 기록됩니다.</p>
 *
 * <p>사용 예시:</p>
 * <pre>
 * {@code
 * @Entity
 * public class MyEntity extends BaseTimeEntity {
 *     // 엔티티 필드 및 메서드
 * }
 * }
 * </pre>
 *
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseTimeEntity {

    /**
     * 엔티티 생성 시간입니다.
     * 엔티티가 처음 저장될 때 자동으로 설정되며, 이후 변경되지 않습니다.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 엔티티 최종 수정 시간입니다.
     * 엔티티가 수정될 때마다 자동으로 현재 시간으로 업데이트됩니다.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}