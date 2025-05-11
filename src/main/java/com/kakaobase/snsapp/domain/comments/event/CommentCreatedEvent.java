package com.kakaobase.snsapp.domain.comments.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.kakaobase.snsapp.global.common.constant.BotConstants;

import java.time.LocalDateTime;

/**
 * 댓글 생성 시 발행되는 이벤트
 *
 * <p>댓글이 생성되었을 때 발행되며, 봇 대댓글 생성 등의 후속 처리에 사용됩니다.</p>
 */
@Getter
@AllArgsConstructor
public class CommentCreatedEvent {

    /**
     * 생성된 댓글 ID
     */
    private final Long commentId;

    /**
     * 댓글이 달린 게시글 ID
     */
    private final Long postId;

    /**
     * 게시글 작성자 ID
     */
    private final Long postAuthorId;

    /**
     * 댓글 작성자 ID
     */
    private final Long commentAuthorId;

    /**
     * 댓글 내용
     */
    private final String content;

    /**
     * 이벤트 생성 시각
     */
    private final LocalDateTime createdAt;

    /**
     * 봇이 작성한 게시글에 달린 댓글인지 확인
     *
     * @return 봇 게시글 여부 (true: 봇 게시글, false: 일반 게시글)
     */
    public boolean isCommentOnBotPost() {
        return postAuthorId != null && postAuthorId.equals(BotConstants.BOT_MEMBER_ID);
    }

    /**
     * 봇이 작성한 댓글인지 확인
     *
     * @return 봇 댓글 여부 (true: 봇이 작성, false: 일반 사용자 작성)
     */
    public boolean isCommentByBot() {
        return commentAuthorId != null && commentAuthorId.equals(BotConstants.BOT_MEMBER_ID);
    }

    /**
     * 이벤트 정보를 로깅용 문자열로 변환
     *
     * @return 로깅용 문자열
     */
    @Override
    public String toString() {
        return String.format("CommentCreatedEvent[commentId=%d, postId=%d, postAuthorId=%d, commentAuthorId=%d]",
                commentId, postId, postAuthorId, commentAuthorId);
    }
}