package com.kakaobase.snsapp.domain.comments.service;

import com.kakaobase.snsapp.domain.comments.converter.CommentConverter;
import com.kakaobase.snsapp.domain.comments.dto.CommentRequestDto;
import com.kakaobase.snsapp.domain.comments.dto.CommentResponseDto;
import com.kakaobase.snsapp.domain.comments.entity.Comment;
import com.kakaobase.snsapp.domain.comments.entity.Recomment;
import com.kakaobase.snsapp.domain.comments.exception.CommentErrorCode;
import com.kakaobase.snsapp.domain.comments.exception.CommentException;
import com.kakaobase.snsapp.domain.comments.repository.CommentRepository;
import com.kakaobase.snsapp.domain.comments.repository.RecommentRepository;
import com.kakaobase.snsapp.domain.members.entity.Member;
import com.kakaobase.snsapp.domain.members.repository.MemberRepository;
import com.kakaobase.snsapp.domain.posts.entity.Post;
import com.kakaobase.snsapp.domain.posts.service.PostService;
import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 댓글 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final RecommentRepository recommentRepository;
    private final MemberRepository memberRepository;
    private final CommentConverter commentConverter;
    private final PostService postService;
    private final CommentLikeService commentLikeService;

    private static final int DEFAULT_PAGE_SIZE = 12;

    /**
     * 댓글을 생성합니다.
     *
     * @param memberId 회원 ID
     * @param postId 게시글 ID
     * @param request 댓글 생성 요청 DTO
     * @return 생성된 댓글 응답 DTO
     */
    @Transactional
    public CommentResponseDto.CreateCommentResponse createComment(Long memberId, Long postId, CommentRequestDto.CreateCommentRequest request) {
        // 게시글 존재 확인
        Post post = postService.findById(postId);

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommentException(GeneralErrorCode.RESOURCE_NOT_FOUND, "memberId", "회원을 찾을 수 없습니다."));

        // 부모 댓글 확인 (대댓글인 경우)
        Comment parentComment = null;
        if (request.parent_id() != null) {
            parentComment = commentRepository.findById(request.parent_id())
                    .orElseThrow(() -> new CommentException(GeneralErrorCode.RESOURCE_NOT_FOUND, "parent_id", "부모 댓글을 찾을 수 없습니다."));

            // 부모 댓글에 대댓글 추가는 1단계만 허용 (대댓글의 대댓글은 불가)
            if (parentComment.getParentComment() != null) {
                throw new CommentException(CommentErrorCode.INVALID_PARENT_COMMENT, "parent_id", "대댓글에는 댓글을 작성할 수 없습니다.");
            }
        }

        // 댓글 엔티티 생성
        Comment comment = commentConverter.toCommentEntity(post, member, request, parentComment);
        Comment savedComment = commentRepository.save(comment);

        log.info("댓글 생성 완료: 댓글 ID={}, 작성자 ID={}, 게시글 ID={}, 부모 댓글 ID={}",
                savedComment.getId(), memberId, postId,
                parentComment != null ? parentComment.getId() : null);

        return commentConverter.toCreateCommentResponse(savedComment);
    }

    /**
     * 댓글을 수정합니다.
     *
     * @param memberId 현재 로그인한 회원 ID
     * @param commentId 수정할 댓글 ID
     * @param request 댓글 수정 요청 DTO
     */
    @Transactional
    public void updateComment(Long memberId, Long commentId, CommentRequestDto.UpdateCommentRequest request) {
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(GeneralErrorCode.RESOURCE_NOT_FOUND, "commentId", "댓글을 찾을 수 없습니다."));

        // 댓글 작성자 확인
        if (!comment.getMember().getId().equals(memberId)) {
            throw new CommentException(CommentErrorCode.POST_NOT_AUTHORIZED, "commentId");
        }

        // 내용 길이 검증
        if (request.content().length() > 2000) {
            throw new CommentException(CommentErrorCode.CONTENT_LENGTH_EXCEEDED);
        }

        // 댓글 내용 업데이트
        comment.updateContent(request.content());

        log.info("댓글 수정 완료: 댓글 ID={}, 작성자 ID={}", commentId, memberId);
    }

    /**
     * 댓글을 삭제합니다.
     *
     * @param memberId 현재 로그인한 회원 ID
     * @param commentId 삭제할 댓글 ID
     */
    @Transactional
    public void deleteComment(Long memberId, Long commentId) {
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(GeneralErrorCode.RESOURCE_NOT_FOUND, "commentId", "댓글을 찾을 수 없습니다."));

        // 댓글 작성자 확인
        if (!comment.getMember().getId().equals(memberId)) {
            throw new CommentException(CommentErrorCode.POST_NOT_AUTHORIZED, "commentId");
        }

        // 댓글의 좋아요 삭제
        commentLikeService.deleteAllCommentLikesByCommentId(commentId);

        // 대댓글이 있는 경우, 대댓글도 함께 삭제
        if (comment.getParentComment() == null) {
            // 일반 댓글인 경우, 연관된 대댓글도 삭제
            List<Recomment> recomments = recommentRepository.findByCommentId(commentId);
            if (!recomments.isEmpty()) {
                // 대댓글 좋아요도 함께 삭제
                commentLikeService.deleteAllRecommentLikesByParentCommentId(commentId);

                // 대댓글 삭제
                recommentRepository.deleteAll(recomments);
                log.info("댓글 삭제 시 연관된 대댓글 {} 개도 함께 삭제: 댓글 ID={}", recomments.size(), commentId);
            }
        }

        // 댓글 삭제
        commentRepository.delete(comment);

        log.info("댓글 삭제 완료: 댓글 ID={}, 삭제자 ID={}", commentId, memberId);
    }

    /**
     * 게시글의 댓글 목록을 조회합니다.
     * API 명세서에 맞게 대댓글은 포함하지 않습니다.
     *
     * @param memberId 현재 로그인한 회원 ID
     * @param postId 게시글 ID
     * @param pageRequest 페이지 요청 DTO
     * @return 댓글 목록 응답 DTO
     */
    public CommentResponseDto.CommentListResponse getCommentsByPostId(Long memberId, Long postId, CommentRequestDto.CommentPageRequest pageRequest) {
        // 게시글 존재 확인
        Post post = postService.findById(postId);

        // 페이지 설정
        int limit = pageRequest.limit() != null ? pageRequest.limit() : DEFAULT_PAGE_SIZE;

        // 댓글 목록 조회
        List<Comment> comments;
        if (pageRequest.cursor() == null) {
            // 첫 페이지 조회
            PageRequest pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
            comments = commentRepository.findByPostId(postId, pageable).getContent();
        } else {
            // 커서 기반 페이징
            comments = commentRepository.findByPostIdAndIdLessThanOrderByIdDesc(
                    postId, pageRequest.cursor(), limit);
        }

        if (comments.isEmpty()) {
            return new CommentResponseDto.CommentListResponse(
                    Collections.emptyList(),
                    false,
                    null
            );
        }

        // 다음 페이지 커서 설정
        Long nextCursor = comments.size() >= limit ? comments.get(comments.size() - 1).getId() : null;

        // 댓글 ID 목록 추출
        List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        // 좋아요 정보 조회
        List<Long> likedCommentIds = commentLikeService.getLikedCommentIdsByMember(memberId, commentIds);
        Set<Long> likedCommentIdsSet = new HashSet<>(likedCommentIds);

        // 응답 DTO 생성 (대댓글 정보 없이)
        List<CommentResponseDto.CommentInfo> commentInfos = comments.stream()
                .map(comment -> commentConverter.toCommentInfo(
                        comment,
                        memberId,
                        likedCommentIdsSet
                ))
                .collect(Collectors.toList());

        return new CommentResponseDto.CommentListResponse(
                commentInfos,
                nextCursor != null,
                nextCursor
        );
    }

    /**
     * 댓글의 대댓글 목록을 조회합니다.
     *
     * @param memberId 현재 로그인한 회원 ID
     * @param commentId 댓글 ID
     * @param pageRequest 페이지 요청 DTO
     * @return 대댓글 목록 응답 DTO
     */
    /**
     * 댓글의 대댓글 목록을 조회합니다.
     *
     * @param memberId 현재 로그인한 회원 ID
     * @param commentId 댓글 ID
     * @param pageRequest 페이지 요청 DTO
     * @return 대댓글 목록 응답 DTO
     */
    public CommentResponseDto.RecommentListResponse getRecommentsByCommentId(Long memberId, Long commentId, CommentRequestDto.RecommentPageRequest pageRequest) {
        // 댓글 존재 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(GeneralErrorCode.RESOURCE_NOT_FOUND, "commentId", "댓글을 찾을 수 없습니다."));

        // 페이지 설정
        int limit = pageRequest.limit() != null ? pageRequest.limit() : DEFAULT_PAGE_SIZE;

        // 대댓글 목록 조회
        List<Recomment> recomments;
        if (pageRequest.cursor() == null) {
            // 첫 페이지 조회 - 생성 시간 오름차순으로 조회
            recomments = recommentRepository.findByCommentIdOrderByCreatedAtAsc(commentId, PageRequest.of(0, limit));
        } else {
            // 커서 기반 페이징 - 커서(ID) 이후부터 생성 시간 오름차순으로 조회
            recomments = recommentRepository.findByCommentIdAndIdGreaterThanOrderByCreatedAtAsc(
                    commentId, pageRequest.cursor(), limit);
        }

        if (recomments.isEmpty()) {
            return new CommentResponseDto.RecommentListResponse(
                    Collections.emptyList(),
                    false,
                    null
            );
        }

        // 다음 페이지 커서 설정
        Long nextCursor = recomments.size() >= limit ? recomments.get(recomments.size() - 1).getId() : null;

        // 대댓글 ID 목록 추출
        List<Long> recommentIds = recomments.stream()
                .map(Recomment::getId)
                .collect(Collectors.toList());

        // 대댓글 좋아요 정보 조회
        List<Long> likedRecommentIds = commentLikeService.getLikedRecommentIdsByMember(memberId, recommentIds);
        Set<Long> likedRecommentIdsSet = new HashSet<>(likedRecommentIds);

        // 응답 DTO 생성
        List<CommentResponseDto.RecommentInfo> recommentInfos = recomments.stream()
                .map(recomment -> commentConverter.toRecommentInfo(
                        recomment,
                        memberId,
                        likedRecommentIdsSet
                ))
                .collect(Collectors.toList());

        return new CommentResponseDto.RecommentListResponse(
                recommentInfos,
                nextCursor != null,
                nextCursor
        );
    }

    /**
     * 댓글 ID로 댓글을 조회합니다.
     *
     * @param commentId 댓글 ID
     * @return 댓글 엔티티
     */
    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(GeneralErrorCode.RESOURCE_NOT_FOUND, "commentId", "댓글을 찾을 수 없습니다."));
    }
}