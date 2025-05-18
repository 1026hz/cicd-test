package com.kakaobase.snsapp.domain.posts.exception;

import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum YoutubeSummaryStatus {
    INVALID_YOUTUBE_URL("invalid_format", PostErrorCode.INVALID_YOUTUBE_URL),
    YOUTUBE_SUBTITLE_NOT_FOUND("subtitles_not_found", PostErrorCode.YOUTUBE_SUBTITLE_NOT_FOUND),
    UNSUPPORTED_SUBTITLE_LANGUAGE("unsupported_subtitle_language", PostErrorCode.UNSUPPORTED_SUBTITLE_LANGUAGE),
    YOUTUBE_VIDEO_PRIVATE("video_private", PostErrorCode.YOUTUBE_VIDEO_PRIVATE),
    YOUTUBE_VIDEO_NOT_FOUND("video_not_found", PostErrorCode.YOUTUBE_VIDEO_NOT_FOUND),
    AI_SERVER_FAILED("internal_server_error", PostErrorCode.INTERNAL_SERVER_ERROR);

    private final String aiErrorCode;
    private final PostErrorCode postErrorCode;

    YoutubeSummaryStatus(String aiErrorCode, PostErrorCode postErrorCode) {
        this.aiErrorCode = aiErrorCode;
        this.postErrorCode = postErrorCode;
    }


    // AI응답 error코드를 enum으로 매핑 메서드
    public static YoutubeSummaryStatus fromAiErrorCode(String aiErrorCode) {
        return Arrays.stream(values())
                .filter(s -> s.aiErrorCode != null && s.aiErrorCode.equals(aiErrorCode))
                .findFirst()
                .orElse(AI_SERVER_FAILED);
    }
}
