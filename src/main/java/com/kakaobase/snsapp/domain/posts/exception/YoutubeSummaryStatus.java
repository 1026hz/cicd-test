package com.kakaobase.snsapp.domain.posts.exception;

import java.util.Arrays;

public enum YoutubeSummaryStatus {
    INVALID_YOUTUBE_URL("invalid_format"),
    YOUTUBE_SUBTITLE_NOT_FOUND("subtitles_not_found"),
    UNSUPPORTED_SUBTITLE_LANGUAGE("unsupported_subtitle_language"),
    YOUTUBE_VIDEO_PRIVATE("video_private"),
    YOUTUBE_VIDEO_NOT_FOUND("video_not_found"),
    AI_SERVER_FAILED("internal_server_error");

    private final String aiErrorCode;

    YoutubeSummaryStatus(String aiErrorCode) {
        this.aiErrorCode = aiErrorCode;
    }

    // AI응답 error코드를 enum으로 매핑 메서드
    public static YoutubeSummaryStatus fromAiErrorCode(String aiErrorCode) {
        return Arrays.stream(values())
                .filter(s -> s.aiErrorCode != null && s.aiErrorCode.equals(aiErrorCode))
                .findFirst()
                .orElse(AI_SERVER_FAILED);
    }
}
