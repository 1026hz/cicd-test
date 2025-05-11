package com.kakaobase.snsapp.domain.comments.converter;

import com.kakaobase.snsapp.domain.comments.dto.CommentRequestDto;
import com.kakaobase.snsapp.domain.comments.dto.CommentResponseDto;
import com.kakaobase.snsapp.domain.comments.entity.Comment;
import com.kakaobase.snsapp.domain.comments.entity.Recomment;
import com.kakaobase.snsapp.domain.comments.exception.CommentErrorCode;
import com.kakaobase.snsapp.domain.comments.exception.CommentException;
import com.kakaobase.snsapp.domain.members.entity.Member;
import com.kakaobase.snsapp.domain.posts.entity.Post;
import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 댓글과 대댓글 관련 엔티티와 DTO 간 변환을 담당하는 컨버터 클래스
 */
@Component
public class CommentConverter {

    /**
     * 댓글 작성 요청 DTO를 댓글 엔티티로 변환
     *
     * @param post 댓글이 작성될 게시글
     * @param member 댓글 작성자
     * @param request 댓글 작성 요청 DTO
     * @return 생성된 댓글 엔티티
     */
    public Comment toCommentEntity(
            Post post,
            Member member,
            CommentRequestDto.CreateCommentRequest request) {
        validateContent(request.content());

        return Comment.builder()
                .post(post)
                .member(member)
                .content(request.content())
                .build();
    }

    /**
     * 대댓글 작성 요청 DTO를 대댓글 엔티티로 변환
     *
     * @param parentComment 대댓글이 작성될 부모 댓글
     * @param member 대댓글 작성자
     * @param request 대댓글 작성 요청 DTO
     * @return 생성된 대댓글 엔티티
     */
    public Recomment toRecommentEntity(Comment parentComment, Member member, CommentRequestDto.CreateCommentRequest request) {
        validateContent(request.content());

        return Recomment.builder()
                .comment(parentComment)
                .member(member)
                .content(request.content())
                .build();
    }

    /**
     * 댓글 엔티티를 댓글 생성 응답 DTO로 변환
     *
     * @param comment 댓글 엔티티
     * @return 댓글 생성 응답 DTO
     */
    public CommentResponseDto.CreateCommentResponse toCreateCommentResponse(Comment comment) {
        CommentResponseDto.UserInfo userInfo = createUserInfo(
                comment.getMember().getId(),
                comment.getMember().getNickname(),
                comment.getMember().getProfileImgUrl()
                // 팔로우 기능은 V2에서 구현 예정
                // false
        );

        return new CommentResponseDto.CreateCommentResponse(
                comment.getId(),
                userInfo,
                comment.getContent(),
                null  // 일반 댓글이므로 parent_id는 null
        );
    }

    /**
     * 대댓글 엔티티를 대댓글 생성 응답 DTO로 변환
     *
     * @param recomment 대댓글 엔티티
     * @return 대댓글 생성 응답 DTO
     */
    public CommentResponseDto.CreateCommentResponse toCreateRecommentResponse(Recomment recomment) {
        CommentResponseDto.UserInfo userInfo = createUserInfo(
                recomment.getMember().getId(),
                recomment.getMember().getNickname(),
                recomment.getMember().getProfileImgUrl()
                // 팔로우 기능은 V2에서 구현 예정
                // false
        );

        return new CommentResponseDto.CreateCommentResponse(
                recomment.getId(),
                userInfo,
                recomment.getContent(),
                recomment.getComment().getId()  // 부모 댓글 ID
        );
    }

    /**
     * 댓글 엔티티를 댓글 상세 정보 DTO로 변환
     *
     * @param comment 댓글 엔티티
     * @param currentMemberId 현재 로그인한 회원 ID
     * @param likedCommentIds 좋아요 누른 댓글 ID 목록
     * @return 댓글 상세 정보 DTO
     */
    public CommentResponseDto.CommentInfo toCommentInfo(
            Comment comment,
            Long currentMemberId,
            Set<Long> likedCommentIds
            // 팔로우 기능은 V2에서 구현 예정
            // Set<Long> followedMemberIds,
    ) {
        CommentResponseDto.UserInfo userInfo = createUserInfo(
                comment.getMember().getId(),
                comment.getMember().getNickname(),
                comment.getMember().getProfileImgUrl()
                // 팔로우 기능은 V2에서 구현 예정
                // followedMemberIds != null && followedMemberIds.contains(comment.getMember().getId())
        );

        return new CommentResponseDto.CommentInfo(
                comment.getId(),
                userInfo,
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getLikeCount(),
                comment.getMember().getId().equals(currentMemberId),
                likedCommentIds != null && likedCommentIds.contains(comment.getId())
        );
    }

    /**
     * 댓글 목록을 댓글 목록 응답 DTO로 변환
     *
     * @param comments 댓글 목록
     * @param currentMemberId 현재 로그인한 회원 ID
     * @param likedCommentIds 좋아요 누른 댓글 ID 목록
     * @param nextCursor 다음 페이지 커서
     * @return 댓글 목록 응답 DTO
     */
    public CommentResponseDto.CommentListResponse toCommentListResponse(
            List<Comment> comments,
            Long currentMemberId,
            Set<Long> likedCommentIds,
            Long nextCursor
    ) {
        List<CommentResponseDto.CommentInfo> commentInfos = comments.stream()
                .map(comment -> toCommentInfo(
                        comment,
                        currentMemberId,
                        likedCommentIds
                ))
                .collect(Collectors.toList());

        return new CommentResponseDto.CommentListResponse(
                commentInfos,
                nextCursor != null,
                nextCursor
        );
    }

    /**
     * 대댓글 목록을 대댓글 목록 응답 DTO로 변환
     *
     * @param recomments 대댓글 목록
     * @param currentMemberId 현재 로그인한 회원 ID
     * @param likedRecommentIds 좋아요 누른 대댓글 ID 목록
     * @param nextCursor 다음 페이지 커서
     * @return 대댓글 목록 응답 DTO
     */
    public CommentResponseDto.RecommentListResponse toRecommentListResponse(
            List<Recomment> recomments,
            Long currentMemberId,
            Set<Long> likedRecommentIds,
            // 팔로우 기능은 V2에서 구현 예정
            // Set<Long> followedMemberIds,
            Long nextCursor
    ) {
        List<CommentResponseDto.RecommentInfo> recommentInfos = recomments.stream()
                .map(recomment -> toRecommentInfo(
                        recomment,
                        currentMemberId,
                        likedRecommentIds
                        // 팔로우 기능은 V2에서 구현 예정
                        // followedMemberIds
                ))
                .collect(Collectors.toList());

        return new CommentResponseDto.RecommentListResponse(
                recommentInfos,
                nextCursor != null,
                nextCursor
        );
    }

    /**
     * 대댓글 엔티티를 대댓글 상세 정보 DTO로 변환
     *
     * @param recomment 대댓글 엔티티
     * @param currentMemberId 현재 로그인한 회원 ID
     * @param likedRecommentIds 좋아요 누른 대댓글 ID 목록
     * @return 대댓글 상세 정보 DTO
     */
    public CommentResponseDto.RecommentInfo toRecommentInfo(
            Recomment recomment,
            Long currentMemberId,
            Set<Long> likedRecommentIds
            // 팔로우 기능은 V2에서 구현 예정
            // Set<Long> followedMemberIds
    ) {
        CommentResponseDto.UserInfo userInfo = createUserInfo(
                recomment.getMember().getId(),
                recomment.getMember().getNickname(),
                recomment.getMember().getProfileImgUrl()
                // 팔로우 기능은 V2에서 구현 예정
                // followedMemberIds != null && followedMemberIds.contains(recomment.getMember().getId())
        );

        return new CommentResponseDto.RecommentInfo(
                recomment.getId(),
                userInfo,
                recomment.getContent(),
                recomment.getCreatedAt(),
                recomment.getLikeCount(),
                recomment.getMember().getId().equals(currentMemberId),
                likedRecommentIds != null && likedRecommentIds.contains(recomment.getId())
        );
    }

    /**
     * 단순 메시지 응답 DTO 생성
     *
     * @param message 메시지
     * @return 메시지 응답 DTO
     */
    public CommentResponseDto.MessageResponse toMessageResponse(String message) {
        return new CommentResponseDto.MessageResponse(message);
    }

    /**
     * 유저 정보 DTO 생성 (중복 코드 제거를 위한 헬퍼 메서드)
     * V2에서 팔로우 기능 구현 시 isFollowed 파라미터 추가 예정
     */
    public CommentResponseDto.UserInfo createUserInfo(Long id, String nickname, String profileImage) {
        return new CommentResponseDto.UserInfo(id, nickname, profileImage);
    }

    /**
     * 댓글/대댓글 내용 유효성 검증
     */
    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new CommentException(GeneralErrorCode.RESOURCE_NOT_FOUND, "content");
        }

        if (content.length() > 2000) {
            throw new CommentException(CommentErrorCode.CONTENT_LENGTH_EXCEEDED);
        }
    }
}