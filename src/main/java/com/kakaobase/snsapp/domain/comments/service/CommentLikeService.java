package com.kakaobase.snsapp.domain.comments.service;

import com.kakaobase.snsapp.domain.comments.converter.CommentConverter;
import com.kakaobase.snsapp.domain.comments.dto.CommentResponseDto;
import com.kakaobase.snsapp.domain.comments.entity.Comment;
import com.kakaobase.snsapp.domain.comments.entity.CommentLike;
import com.kakaobase.snsapp.domain.comments.entity.Recomment;
import com.kakaobase.snsapp.domain.comments.entity.RecommentLike;
import com.kakaobase.snsapp.domain.comments.exception.CommentErrorCode;
import com.kakaobase.snsapp.domain.comments.exception.CommentException;
import com.kakaobase.snsapp.domain.comments.repository.CommentLikeRepository;
import com.kakaobase.snsapp.domain.comments.repository.CommentRepository;
import com.kakaobase.snsapp.domain.comments.repository.RecommentLikeRepository;
import com.kakaobase.snsapp.domain.comments.repository.RecommentRepository;
import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 댓글 및 대댓글 좋아요 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final RecommentRepository recommentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final RecommentLikeRepository recommentLikeRepository;
    private final CommentConverter commentConverter;

    /**
     * 댓글 좋아요 토글 처리
     * 이미 좋아요가 있으면 취소, 없으면 추가합니다.
     *
     * @param memberId 회원 ID
     * @param commentId 댓글 ID
     * @return 좋아요 토글 결과 응답 DTO
     */
    @Transactional
    public CommentResponseDto.CommentLikeResponse toggleCommentLike(Long memberId, Long commentId) {
        // 댓글 존재 여부 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentException(GeneralErrorCode.RESOURCE_NOT_FOUND, "commentId", "댓글을 찾을 수 없습니다."));

        boolean isLiked = commentLikeRepository.existsByMemberIdAndCommentId(memberId, commentId);

        if (isLiked) {
            // 좋아요 취소
            Optional<CommentLike> commentLikeOpt = commentLikeRepository.findByMemberIdAndCommentId(memberId, commentId);
            if (commentLikeOpt.isPresent()) {
                commentLikeRepository.delete(commentLikeOpt.get());
                comment.decreaseLikeCount();
                log.info("댓글 좋아요 취소: 댓글 ID={}, 회원 ID={}", commentId, memberId);
            }
        } else {
            // 좋아요 추가
            CommentLike commentLike = new CommentLike(memberId, commentId);
            commentLikeRepository.save(commentLike);
            comment.increaseLikeCount();
            log.info("댓글 좋아요 추가: 댓글 ID={}, 회원 ID={}", commentId, memberId);
        }

        // 업데이트된 상태 반환
        return new CommentResponseDto.CommentLikeResponse(
                !isLiked,
                comment.getLikeCount()
        );
    }

    /**
     * 대댓글 좋아요 토글 처리
     * 이미 좋아요가 있으면 취소, 없으면 추가합니다.
     *
     * @param memberId 회원 ID
     * @param recommentId 대댓글 ID
     * @return 좋아요 토글 결과 응답 DTO
     */
    @Transactional
    public CommentResponseDto.RecommentLikeResponse toggleRecommentLike(Long memberId, Long recommentId) {
        // 대댓글 존재 여부 확인
        Recomment recomment = recommentRepository.findById(recommentId)
                .orElseThrow(() -> new CommentException(GeneralErrorCode.RESOURCE_NOT_FOUND, "recommentId", "대댓글을 찾을 수 없습니다."));

        boolean isLiked = recommentLikeRepository.existsByMemberIdAndRecommentId(memberId, recommentId);

        if (isLiked) {
            // 좋아요 취소
            Optional<RecommentLike> recommentLikeOpt = recommentLikeRepository.findByMemberIdAndRecommentId(memberId, recommentId);
            if (recommentLikeOpt.isPresent()) {
                recommentLikeRepository.delete(recommentLikeOpt.get());
                recomment.decreaseLikeCount();
                log.info("대댓글 좋아요 취소: 대댓글 ID={}, 회원 ID={}", recommentId, memberId);
            }
        } else {
            // 좋아요 추가
            RecommentLike recommentLike = new RecommentLike(memberId, recommentId);
            recommentLikeRepository.save(recommentLike);
            recomment.increaseLikeCount();
            log.info("대댓글 좋아요 추가: 대댓글 ID={}, 회원 ID={}", recommentId, memberId);
        }

        // 업데이트된 상태 반환
        return new CommentResponseDto.RecommentLikeResponse(
                !isLiked,
                recomment.getLikeCount()
        );
    }

    /**
     * 회원이 특정 댓글 목록 중 좋아요한 댓글 ID 목록을 조회합니다.
     *
     * @param memberId 회원 ID
     * @param commentIds 댓글 ID 목록
     * @return 좋아요한 댓글 ID 목록
     */
    public List<Long> getLikedCommentIdsByMember(Long memberId, List<Long> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            return Collections.emptyList();
        }

        return commentLikeRepository.findCommentIdsByMemberIdAndCommentIdIn(memberId, commentIds);
    }

    /**
     * 회원이 특정 대댓글 목록 중 좋아요한 대댓글 ID 목록을 조회합니다.
     *
     * @param memberId 회원 ID
     * @param recommentIds 대댓글 ID 목록
     * @return 좋아요한 대댓글 ID 목록
     */
    public List<Long> getLikedRecommentIdsByMember(Long memberId, List<Long> recommentIds) {
        if (recommentIds == null || recommentIds.isEmpty()) {
            return Collections.emptyList();
        }

        return recommentLikeRepository.findRecommentIdsByMemberIdAndRecommentIdIn(memberId, recommentIds);
    }

    /**
     * 회원이 댓글을 좋아요했는지 확인합니다.
     *
     * @param memberId 회원 ID
     * @param commentId 댓글 ID
     * @return 좋아요 여부
     */
    public boolean isCommentLikedByMember(Long memberId, Long commentId) {
        return commentLikeRepository.existsByMemberIdAndCommentId(memberId, commentId);
    }

    /**
     * 회원이 대댓글을 좋아요했는지 확인합니다.
     *
     * @param memberId 회원 ID
     * @param recommentId 대댓글 ID
     * @return 좋아요 여부
     */
    public boolean isRecommentLikedByMember(Long memberId, Long recommentId) {
        return recommentLikeRepository.existsByMemberIdAndRecommentId(memberId, recommentId);
    }

    /**
     * 댓글 삭제 시 연관된 좋아요를 일괄 삭제합니다.
     *
     * @param commentId 댓글 ID
     */
    @Transactional
    public void deleteAllCommentLikesByCommentId(Long commentId) {
        int deletedCount = commentLikeRepository.deleteByCommentId(commentId);
        log.info("댓글 관련 좋아요 일괄 삭제 완료: 댓글 ID={}, 삭제된 좋아요 수={}", commentId, deletedCount);
    }

    /**
     * 대댓글 삭제 시 연관된 좋아요를 일괄 삭제합니다.
     *
     * @param recommentId 대댓글 ID
     */
    @Transactional
    public void deleteAllRecommentLikesByRecommentId(Long recommentId) {
        int deletedCount = recommentLikeRepository.deleteByRecommentId(recommentId);
        log.info("대댓글 관련 좋아요 일괄 삭제 완료: 대댓글 ID={}, 삭제된 좋아요 수={}", recommentId, deletedCount);
    }

    /**
     * 댓글이 삭제될 때 해당 댓글에 달린 모든 대댓글의 좋아요를 일괄 삭제합니다.
     *
     * @param commentId 댓글 ID
     */
    @Transactional
    public void deleteAllRecommentLikesByParentCommentId(Long commentId) {
        // 대댓글 ID 목록 조회
        List<Long> recommentIds = recommentRepository.findByCommentId(commentId)
                .stream()
                .map(Recomment::getId)
                .toList();

        if (!recommentIds.isEmpty()) {
            int deletedCount = recommentLikeRepository.deleteByRecommentIdIn(recommentIds);
            log.info("댓글 관련 대댓글 좋아요 일괄 삭제 완료: 댓글 ID={}, 삭제된 좋아요 수={}", commentId, deletedCount);
        }
    }
}