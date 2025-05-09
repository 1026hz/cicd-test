package com.kakaobase.snsapp.domain.comments.controller;

import com.kakaobase.snsapp.domain.comments.converter.CommentConverter;
import com.kakaobase.snsapp.domain.comments.converter.LikeConverter;
import com.kakaobase.snsapp.domain.comments.dto.CommentRequestDto;
import com.kakaobase.snsapp.domain.comments.dto.CommentResponseDto;
import com.kakaobase.snsapp.domain.comments.service.CommentService;
import com.kakaobase.snsapp.domain.comments.service.LikeService;
import com.kakaobase.snsapp.domain.members.entity.Member;
import com.kakaobase.snsapp.domain.members.service.MemberService;
import com.kakaobase.snsapp.global.common.response.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "댓글 API", description = "게시글 댓글 관련 API")
public class CommentController {

    private final CommentService commentService;
    private final LikeService likeService;
    private final MemberService memberService;

    /**
     * 현재 로그인한 사용자의 Member 객체를 반환
     *
     * @return 현재 로그인한 사용자의 Member 객체
     */
    private Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return memberService.getMemberByUsername(username);
    }

    /**
     * 댓글 작성 API
     */
    @PostMapping("/posts/{postId}/comments")
    @Operation(
            summary = "댓글 작성",
            description = "게시글에 댓글을 작성합니다. parentId가 없으면 일반 댓글, 있으면 대댓글로 등록됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "댓글 작성 성공",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.CreateCommentResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.ErrorResponse.class)))
    })
    public ResponseEntity<CustomResponse<CommentResponseDto.CreateCommentResponse>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequestDto.CreateCommentRequest request
    ) {
        Member member = getCurrentMember();
        CommentResponseDto.CreateCommentResponse response = commentService.createComment(member, postId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CustomResponse.success("댓글이 작성되었습니다.", response));
    }

    /**
     * 댓글 수정 API
     */
    @PutMapping("/comments/{commentId}")
    @Operation(
            summary = "댓글 수정",
            description = "댓글 내용을 수정합니다. 자신이 작성한 댓글만 수정할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글 없음",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.ErrorResponse.class)))
    })
    public ResponseEntity<CustomResponse<CommentResponseDto.MessageResponse>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequestDto.UpdateCommentRequest request
    ) {
        Member member = getCurrentMember();
        commentService.updateComment(member.getId(), commentId, request);
        return ResponseEntity.ok(CustomResponse.success("댓글이 수정되었습니다."));
    }

    /**
     * 댓글 삭제 API
     */
    @DeleteMapping("/comments/{commentId}")
    @Operation(
            summary = "댓글 삭제",
            description = "댓글을 삭제합니다. 자신이 작성한 댓글만 삭제할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.MessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글 없음",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.ErrorResponse.class)))
    })
    public ResponseEntity<CustomResponse<CommentResponseDto.MessageResponse>> deleteComment(
            @PathVariable Long commentId
    ) {
        Member member = getCurrentMember();
        commentService.deleteComment(member.getId(), commentId);
        return ResponseEntity.ok(CustomResponse.success("댓글이 삭제되었습니다."));
    }

    /**
     * 게시글의 댓글 목록 조회 API
     */
    @GetMapping("/posts/{postId}/comments")
    @Operation(
            summary = "게시글의 댓글 목록 조회",
            description = "게시글에 작성된 댓글 목록을 조회합니다. 페이지네이션을 지원합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.CommentListResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글 없음",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.ErrorResponse.class)))
    })
    public ResponseEntity<CustomResponse<CommentResponseDto.CommentListResponse>> getCommentsByPostId(
            @PathVariable Long postId,
            @Parameter(description = "한 번에 불러올 댓글 수 (기본값: 12)") @RequestParam(required = false) Integer limit,
            @Parameter(description = "페이지네이션 커서 (이전 응답의 next_cursor)") @RequestParam(required = false) Long cursor
    ) {
        Member member = getCurrentMember();
        CommentRequestDto.CommentPageRequest pageRequest = new CommentRequestDto.CommentPageRequest(limit, cursor);
        CommentResponseDto.CommentListResponse response = commentService.getCommentsByPostId(member.getId(), postId, pageRequest);
        return ResponseEntity.ok(CustomResponse.success("댓글 목록을 조회했습니다.", response));
    }

    /**
     * 댓글의 대댓글 목록 조회 API
     */
    @GetMapping("/comments/{commentId}/recomments")
    @Operation(
            summary = "댓글의 대댓글 목록 조회",
            description = "특정 댓글에 작성된 대댓글 목록을 조회합니다. 페이지네이션을 지원합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대댓글 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.RecommentListResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글 없음",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.ErrorResponse.class)))
    })
    public ResponseEntity<CustomResponse<CommentResponseDto.RecommentListResponse>> getRecommentsByCommentId(
            @PathVariable Long commentId,
            @Parameter(description = "한 번에 불러올 대댓글 수 (기본값: 12)") @RequestParam(required = false) Integer limit,
            @Parameter(description = "페이지네이션 커서 (이전 응답의 next_cursor)") @RequestParam(required = false) Long cursor
    ) {
        Member member = getCurrentMember();
        CommentRequestDto.RecommentPageRequest pageRequest = new CommentRequestDto.RecommentPageRequest(limit, cursor);
        CommentResponseDto.RecommentListResponse response = commentService.getRecommentsByCommentId(member.getId(), commentId, pageRequest);
        return ResponseEntity.ok(CustomResponse.success("대댓글 목록을 조회했습니다.", response));
    }

    /**
     * 댓글 좋아요 토글 API
     */
    @PostMapping("/comments/{commentId}/like")
    @Operation(
            summary = "댓글 좋아요 토글",
            description = "댓글에 좋아요를 누르거나 취소합니다. 이미 좋아요가 되어있으면 취소되고, 없으면 추가됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 토글 성공",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.CommentLikeResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글 없음",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.ErrorResponse.class)))
    })
    public ResponseEntity<CustomResponse<CommentResponseDto.CommentLikeResponse>> toggleCommentLike(
            @PathVariable Long commentId
    ) {
        Member member = getCurrentMember();
        CommentResponseDto.CommentLikeResponse response = likeService.toggleCommentLike(member.getId(), commentId);
        String message = response.liked() ? "댓글에 좋아요를 눌렀습니다." : "댓글 좋아요를 취소했습니다.";
        return ResponseEntity.ok(CustomResponse.success(message, response));
    }

    /**
     * 대댓글 좋아요 토글 API
     */
    @PostMapping("/recomments/{recommentId}/like")
    @Operation(
            summary = "대댓글 좋아요 토글",
            description = "대댓글에 좋아요를 누르거나 취소합니다. 이미 좋아요가 되어있으면 취소되고, 없으면 추가됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 토글 성공",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.RecommentLikeResponse.class))),
            @ApiResponse(responseCode = "404", description = "대댓글 없음",
                    content = @Content(schema = @Schema(implementation = CommentResponseDto.ErrorResponse.class)))
    })
    public ResponseEntity<CustomResponse<CommentResponseDto.RecommentLikeResponse>> toggleRecommentLike(
            @PathVariable Long recommentId
    ) {
        Member member = getCurrentMember();
        CommentResponseDto.RecommentLikeResponse response = likeService.toggleRecommentLike(member.getId(), recommentId);
        String message = response.liked() ? "대댓글에 좋아요를 눌렀습니다." : "대댓글 좋아요를 취소했습니다.";
        return ResponseEntity.ok(CustomResponse.success(message, response));
    }
}