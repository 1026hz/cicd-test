package com.kakaobase.snsapp.global.constants;
import com.kakaobase.snsapp.domain.members.entity.Member;

public class MemberFixtureConstants {

    // 기본 Mock 데이터
    public static final Long MEMBER_ID = 1L;
    public static final String MEMBER_EMAIL = "test@kakao.com";
    public static final String MEMBER_NAME = "테스트유저";
    public static final String MEMBER_NICKNAME = "테스터";
    public static final String MEMBER_PASSWORD = "$2a$12$encodedPassword";
    public static final String MEMBER_GITHUB_URL = "https://github.com/testuser";
    public static final String MEMBER_PROFILE_IMG_URL = "https://example.com/profile/test-user.jpg";
    public static final Member.ClassName KBT_MEMBER_CLASS_NAME = Member.ClassName.PANGYO_1;
    public static final Member.ClassName NON_KBT_MEMBER_CLASS_NAME = Member.ClassName.ALL;
    public static final Member.Role MEMBER_ROLE = Member.Role.USER;
    public static final Boolean MEMBER_IS_BANNED = false;
    public static final Integer MEMBER_FOLLOWING_COUNT = 0;
    public static final Integer MEMBER_FOLLOWER_COUNT = 0;

    // 관리자 Mock 데이터
    public static final Long ADMIN_ID = 2L;
    public static final String ADMIN_EMAIL = "admin@kakao.com";
    public static final String ADMIN_NICKNAME = "관리자";
    public static final Member.Role ADMIN_ROLE = Member.Role.ADMIN;

    // 밴된 유저 Mock 데이터
    public static final Long BANNED_MEMBER_ID = 3L;
    public static final String BANNED_MEMBER_EMAIL = "banned@kakao.com";
    public static final String BANNED_MEMBER_NICKNAME = "밴유저";
    public static final Boolean BANNED_MEMBER_IS_BANNED = true;

    // 봇 계정 Mock 데이터
    public static final Long BOT_ID = 4L;
    public static final String BOT_EMAIL = "bot@kakao.com";
    public static final String BOT_NICKNAME = "KakaoBot";
    public static final Member.Role BOT_ROLE = Member.Role.BOT;

    // 팔로우 관련 Mock 데이터
    public static final Integer FOLLOWING_COUNT = 10;
    public static final Integer FOLLOWER_COUNT = 20;

    private MemberFixtureConstants() {
        // 유틸리티 클래스 - 인스턴스화 방지
    }
}
