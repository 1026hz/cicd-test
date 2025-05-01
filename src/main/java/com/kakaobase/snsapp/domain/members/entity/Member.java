package com.kakaobase.snsapp.domain.members.entity;

import com.kakaobase.snsapp.global.common.entity.BaseUpdateTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@SQLDelete(sql = "UPDATE members SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Member extends BaseUpdateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'USER'")
    private Role role;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(name = "class_name", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ClassName className;

    @Column(name = "profile_img_url", length = 512)
    private String profileImgUrl;

    @Column(name = "github_url", length = 255)
    private String githubUrl;

    @Column(name = "is_banned")
    @ColumnDefault("false")
    private Boolean isBanned;

    @Column(name = "following_count", nullable = false)
    @ColumnDefault("0")
    private Integer followingCount;

    @Column(name = "follower_count", nullable = false)
    @ColumnDefault("0")
    private Integer followerCount;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 회원 역할을 정의하는 열거형입니다.
     */
    public enum Role {
        USER,   // 일반 사용자
        ADMIN,  // 관리자
        BOT     // 봇
    }

    /**
     * 카카오 테크 부트캠프 기수를 정의하는 열거형입니다.
     */
    public enum ClassName {
        PANGYO_1,
        PANGYO_2,
        JEJU_1,
        JEJU_2,
        JEJU_3
    }

    /**
     * 회원 엔티티를 생성합니다. 커스텀 Converter에서 사용됩니다.
     */
    public Member(String email, String password, String name, String nickname,
                  ClassName className, String profileImgUrl, String githubUrl, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.className = className;
        this.profileImgUrl = profileImgUrl;
        this.githubUrl = githubUrl;
        this.role = (role != null) ? role : Role.USER;
        this.isBanned = false;
        this.followingCount = 0;
        this.followerCount = 0;
    }

    /**
     * 회원 프로필 이미지를 업데이트합니다.
     */
    public void updateProfile(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    /**
     * GitHub URL을 업데이트합니다.
     */
    public void updateGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    /**
     * 비밀번호를 업데이트합니다.
     */
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 회원 역할을 업데이트합니다.
     */
    public void updateRole(Role role) {
        this.role = role;
    }

    /**
     * 회원 밴 상태를 변경합니다.
     */
    public void updateBanStatus(boolean isBanned) {
        this.isBanned = isBanned;
    }

    /**
     * 팔로잉 카운트를 증가시킵니다.
     */
    public void incrementFollowingCount() {
        this.followingCount++;
    }

    /**
     * 팔로잉 카운트를 감소시킵니다.
     */
    public void decrementFollowingCount() {
        if (this.followingCount > 0) {
            this.followingCount--;
        }
    }

    /**
     * 팔로워 카운트를 증가시킵니다.
     */
    public void incrementFollowerCount() {
        this.followerCount++;
    }

    /**
     * 팔로워 카운트를 감소시킵니다.
     */
    public void decrementFollowerCount() {
        if (this.followerCount > 0) {
            this.followerCount--;
        }
    }

    /**
     * 회원 역할을 문자열로 반환합니다.
     * JWT 토큰에 반환하는 타입 호환성을 위해 별도로 선언됩니다.
     *
     * @return 역할 이름(String)
     */
    public String getRole() {
        return this.role.name();
    }

    /**
     * 회원 기수를 문자열로 반환합니다.
     * JWT 토큰에 저장하기 위한 용도로 사용됩니다.
     *
     * @return 기수 이름(String)
     */
    public String getClassName() {
        return this.className.name();
    }

    /**
     * 계정이 삭제되었는지 확인합니다.
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * 계정이 활성화되어 있는지 확인합니다.
     * (삭제되지 않았고, 밴 상태가 아닌 경우)
     */
    public boolean isEnabled() {
        return !isDeleted() && (isBanned == null || !isBanned);
    }
}