package com.kakaobase.snsapp.domain.comments.converter;

import com.kakaobase.snsapp.domain.comments.dto.BotRecommentRequestDto;
import com.kakaobase.snsapp.domain.comments.dto.CommentRequestDto;
import com.kakaobase.snsapp.domain.comments.dto.CommentResponseDto;
import com.kakaobase.snsapp.domain.comments.entity.Comment;
import com.kakaobase.snsapp.domain.posts.entity.Post;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 봇 대댓글 관련 엔티티와 DTO 간 변환을 담당하는 컨버터 클래스
 */
public class BotRecommentConverter {

    /**
     * AI 서버 요청 DTO 생성 (RecommentInfo 사용)
     *
     * @param post 게시글 엔티티
     * @param postAuthorInfo 게시글 작성자 봇 정보
     * @param comment 댓글 엔티티
     * @param recomments 기존 대댓글 목록 (RecommentInfo)
     * @return AI 서버 요청 DTO
     */
    public static BotRecommentRequestDto.CreateRecommentRequest toCreateRecommentRequest(
            Post post,
            BotRecommentRequestDto.UserInfo postAuthorInfo,
            Comment comment,
            List<CommentResponseDto.RecommentInfo> recomments) {

        String boardType = post.getBoardType().name();

        // 게시글 정보 생성
        BotRecommentRequestDto.BotPost botPost = new BotRecommentRequestDto.BotPost(
                post.getId(),
                postAuthorInfo,
                formatDateTime(post.getCreatedAt()),
                post.getContent()
        );

        // 댓글 정보 생성
        BotRecommentRequestDto.BotComment botComment = new BotRecommentRequestDto.BotComment(
                comment.getId(),
                new BotRecommentRequestDto.UserInfo(
                        comment.getMember().getNickname(),
                        comment.getMember().getClassName()
                ),
                formatDateTime(comment.getCreatedAt()),
                comment.getContent(),
                toRecommentListFromDto(recomments)
        );

        return new BotRecommentRequestDto.CreateRecommentRequest(
                boardType,
                botPost,
                botComment
        );
    }

    /**
     * RecommentInfo DTO 리스트를 BotRecomment DTO 리스트로 변환
     *
     * @param recomments 대댓글 정보 리스트
     * @return BotRecomment DTO 리스트
     */
    private static List<BotRecommentRequestDto.BotRecomment> toRecommentListFromDto(
            List<CommentResponseDto.RecommentInfo> recomments) {

        if (recomments == null || recomments.isEmpty()) {
            return List.of();
        }

        return recomments.stream()
                .map(recomment -> new BotRecommentRequestDto.BotRecomment(
                        new BotRecommentRequestDto.UserInfo(
                                recomment.user().nickname(),
                                "PANGYO_2"  // TODO: RecommentInfo에 className 추가 필요
                        ),
                        recomment.created_at().format(DateTimeFormatter.ISO_INSTANT),
                        recomment.content()
                ))
                .collect(Collectors.toList());
    }

    /**
     * AI 응답 DTO를 대댓글 생성 요청 DTO로 변환
     *
     * @param response AI 서버 응답
     * @param commentId 부모 댓글 ID (parent_id로 사용)
     * @return 대댓글 생성 요청 DTO
     */
    public static CommentRequestDto.CreateCommentRequest toCreateCommentRequest(
            BotRecommentRequestDto.AiRecommentResponse response,
            Long commentId) {

        return new CommentRequestDto.CreateCommentRequest(
                response.data().content(),
                commentId  // parent_id로 사용됨
        );
    }

    /**
     * LocalDateTime을 ISO_INSTANT 형식 문자열로 변환
     *
     * @param dateTime 변환할 시간
     * @return ISO_INSTANT 형식 문자열 (예: 2025-04-27T13:41:32.311141Z)
     */
    private static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * AI 응답이 유효한지 검증
     *
     * @param response AI 서버 응답
     * @return 유효하면 true, 아니면 false
     */
    public static boolean isValidResponse(BotRecommentRequestDto.AiRecommentResponse response) {
        if (response == null || response.data() == null) {
            return false;
        }

        String content = response.data().content();
        return content != null && !content.trim().isEmpty() && content.length() <= 2000;
    }

    /**
     * 에러 응답 메시지 생성
     *
     * @param errorResponse AI 서버 에러 응답
     * @return 에러 메시지
     */
    public static String getErrorMessage(BotRecommentRequestDto.ErrorResponse errorResponse) {
        if (errorResponse == null) {
            return "알 수 없는 오류가 발생했습니다.";
        }

        StringBuilder message = new StringBuilder(errorResponse.message());

        if (errorResponse.field() != null && !errorResponse.field().isEmpty()) {
            message.append(" (필드: ")
                    .append(String.join(", ", errorResponse.field()))
                    .append(")");
        }

        return message.toString();
    }

    /**
     * AI 응답에서 봇 정보가 유효한지 검증
     *
     * @param response AI 서버 응답
     * @return 유효하면 true, 아니면 false
     */
    public static boolean isBotResponseValid(BotRecommentRequestDto.AiRecommentResponse response) {
        if (response == null || response.data() == null || response.data().user() == null) {
            return false;
        }

        // AI 응답의 user 정보 검증
        BotRecommentRequestDto.UserInfo botUser = response.data().user();
        return botUser.nickname() != null && !botUser.nickname().trim().isEmpty();
    }

    /**
     * AI 요청 데이터 로깅용 문자열 생성
     *
     * @param request AI 서버 요청 DTO
     * @return 로깅용 문자열
     */
    public static String toLogString(BotRecommentRequestDto.CreateRecommentRequest request) {
        if (request == null) {
            return "null request";
        }

        return String.format("boardType: %s, postId: %d, commentId: %d, recomments: %d개",
                request.boardType(),
                request.post() != null ? request.post().id() : null,
                request.comment() != null ? request.comment().id() : null,
                request.comment() != null && request.comment().recomments() != null
                        ? request.comment().recomments().size() : 0
        );
    }

    /**
     * AI 응답 데이터 로깅용 문자열 생성
     *
     * @param response AI 서버 응답 DTO
     * @return 로깅용 문자열
     */
    public static String toLogString(BotRecommentRequestDto.AiRecommentResponse response) {
        if (response == null || response.data() == null) {
            return "null response";
        }

        return String.format("postId: %d, commentId: %d, content: %s...",
                response.data().postId(),
                response.data().commentId(),
                response.data().content() != null && response.data().content().length() > 20
                        ? response.data().content().substring(0, 20)
                        : response.data().content()
        );
    }
}