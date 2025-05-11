package com.kakaobase.snsapp.global.common.constant;

/**
 * 봇 관련 상수 정의
 *
 * <p>AI 소셜봇 기능에 사용되는 상수들을 중앙 관리합니다.</p>
 */
public final class BotConstants {

    /**
     * 인스턴스화 방지를 위한 private 생성자
     */
    private BotConstants() {
        throw new AssertionError("BotConstants는 인스턴스화할 수 없습니다.");
    }

    /**
     * 봇 멤버 ID
     *
     * <p>AI 소셜봇의 고정 멤버 ID입니다.</p>
     */
    public static final Long BOT_MEMBER_ID = 1213L;

    /**
     * 봇 닉네임
     *
     * <p>AI 소셜봇의 기본 닉네임입니다.</p>
     */
    public static final String BOT_NICKNAME = "roro.bot";

    /**
     * 봇 기수
     *
     * <p>AI 소셜봇이 속한 기본 기수입니다.</p>
     */
    public static final String BOT_CLASS_NAME = "PANGYO_2";

    /**
     * 봇 게시글 생성 임계값
     *
     * <p>일반 사용자 게시글이 이 수만큼 누적되면 봇이 게시글을 작성합니다.</p>
     */
    public static final int POST_COUNT_THRESHOLD = 5;

    /**
     * AI 서버 요청 타임아웃 (초)
     *
     * <p>AI 서버 응답을 기다리는 최대 시간입니다.</p>
     */
    public static final int AI_SERVER_TIMEOUT_SECONDS = 30;

    /**
     * 봇 게시글 최대 길이
     *
     * <p>AI가 생성하는 게시글의 최대 문자 수입니다.</p>
     */
    public static final int BOT_POST_MAX_LENGTH = 2000;
}