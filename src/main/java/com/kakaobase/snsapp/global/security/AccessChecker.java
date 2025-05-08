package com.kakaobase.snsapp.global.security;

import com.kakaobase.snsapp.domain.posts.converter.PostConverter;
import com.kakaobase.snsapp.domain.posts.entity.Post;
import com.kakaobase.snsapp.domain.posts.exception.PostException;
import com.kakaobase.snsapp.domain.posts.repository.PostRepository;
import com.kakaobase.snsapp.global.error.code.GeneralErrorCode;
import com.kakaobase.snsapp.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 게시판 및 게시글 접근 권한을 검증하는 클래스
 * Spring Security의 @PreAuthorize 어노테이션과 함께 사용됩니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccessChecker {

    private final PostRepository postRepository;

    /**
     * 사용자가 특정 게시판에 접근할 권한이 있는지 검증합니다.
     *
     * @param postType 게시판 타입 (예: all, pangyo_1, jeju_2)
     * @return 접근 가능하면 true, 아니면 false
     */
    public boolean hasAccessToBoard(String postType) {
        // 인증되지 않은 사용자는 'all' 게시판만 접근 가능
        if (!SecurityUtil.isAuthenticated()) {
            return "all".equalsIgnoreCase(postType);
        }

        // 관리자, 봇 권한이 있는 경우 모든 게시판 접근 가능
        if (SecurityUtil.isAdminOrBot()) {
            return true;
        }

        // 'all' 게시판은 모든 인증된 사용자 접근 가능
        if ("all".equalsIgnoreCase(postType)) {
            return true;
        }

        // 사용자의 기수(className) 확인
        String className = SecurityUtil.getMemberClassName()
                .orElseThrow(()-> new CustomException(GeneralErrorCode.RESOURCE_NOT_FOUND, "class_name", "인증객체에서 회원의 기수명을 찾을 수 없습니다"));

        if (!StringUtils.hasText(className)) {
            Long memberId = SecurityUtil.getMemberIdAsLong()
                    .orElseThrow(()-> new CustomException(GeneralErrorCode.RESOURCE_NOT_FOUND, "memberId", "인증 객체에서 회원의 Id을 찾을 수 없습니다"));
            log.warn("사용자 ID {}의 기수 정보가 없습니다.", memberId);
            return false;
        }

        // postType과 사용자의 기수 일치 여부 확인
        String normalizedPostType = postType.toUpperCase();

        boolean hasAccess = className.equals(normalizedPostType);

        if (!hasAccess) {
            log.debug("사용자 ID {}(기수: {})의 게시판 접근 거부: {}",
                    SecurityUtil.getMemberIdAsLong(), className, postType);
        }

        return hasAccess;
    }

    /**
     * 사용자가 게시글의 소유자인지 검증합니다.
     *
     * @param postId 게시글 ID
     * @param authentication 인증 정보
     * @return 소유자이면 true, 아니면 false
     */
    public boolean isPostOwner(Long postId, Authentication authentication) {
        // 관리자, 봇 권한이 있는 경우 소유자로 취급
        if (SecurityUtil.isAdminOrBot()) {
            return true;
        }

        // 사용자 ID 확인
        Long memberId = SecurityUtil.getMemberIdAsLong()
                .orElseThrow(()-> new CustomException(GeneralErrorCode.RESOURCE_NOT_FOUND, "memberId", "인증 객체에서 회원의 Id을 찾을 수 없습니다"));
        if (memberId == null) {
            return false;
        }

        // 게시글 조회
        return postRepository.findByIdAndMemberId(postId, memberId).isPresent();
    }

    /**
     * 문자열 형태의 postType을 BoardType enum으로 변환합니다.
     *
     * @param postType 게시판 타입 문자열
     * @return BoardType enum 값
     * @throws PostException 유효하지 않은 postType인 경우
     */
    public Post.BoardType convertToBoardType(String postType) {
        try {
            return PostConverter.toBoardType(postType);
        } catch (IllegalArgumentException e) {
            throw new PostException(GeneralErrorCode.RESOURCE_NOT_FOUND, "postType");
        }
    }
}