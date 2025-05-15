package com.gujo.uminity.comment.controller;

import com.gujo.uminity.comment.dto.request.CommentCreateRequest;
import com.gujo.uminity.comment.dto.request.CommentListRequest;
import com.gujo.uminity.comment.dto.request.CommentUpdateRequest;
import com.gujo.uminity.comment.dto.response.CommentResponseDto;
import com.gujo.uminity.comment.service.CommentService;
import com.gujo.uminity.common.web.PageResponse;
import com.gujo.uminity.common.security.MyUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {
    private final CommentService commentService;

    // 1. 댓글 목록 조회
    @GetMapping
    public ResponseEntity<PageResponse<CommentResponseDto>> listComments(
            @PathVariable Long postId,
            @Valid @ModelAttribute CommentListRequest req
    ) {
        PageResponse<CommentResponseDto> page = commentService.listComments(postId, req);
        return ResponseEntity.ok(page);
    }

    // 2. 댓글 생성 (인증된 사용자만)
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest req,
            @AuthenticationPrincipal MyUserDetails principal
    ) {
        CommentResponseDto created = commentService.createComment(postId, req, principal.getUserId());
        return ResponseEntity
                .created(URI.create("/api/posts/" + postId + "/comments/" + created.getCommentId()))
                .body(created);
    }

    // 3. 댓글 수정 (작성자만)
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest req,
            @AuthenticationPrincipal MyUserDetails principal
    ) {
        CommentResponseDto updated = commentService.updateComment(postId, commentId, req, principal.getUserId());
        return ResponseEntity.ok(updated);
    }

    // 4. 댓글 삭제 (작성자만)
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal MyUserDetails principal
    ) {
        commentService.deleteComment(postId, commentId, principal.getUserId());
        return ResponseEntity.noContent().build();
    }

    /*
    잘못된 URI 사용을 방지하기 위해 postId를 넣어줘서 존재하지 않는 댓글이랑
    게시글에 속하지않는 댓글을 구분해서 404와 400 을 구분하자

     */
}