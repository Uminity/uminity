package com.gujo.uminity.comment.controller;

import com.gujo.uminity.comment.dto.request.CommentCreateRequest;
import com.gujo.uminity.comment.dto.request.CommentListRequest;
import com.gujo.uminity.comment.dto.request.CommentUpdateRequest;
import com.gujo.uminity.comment.dto.response.CommentResponseDto;
import com.gujo.uminity.comment.service.CommentService;
import com.gujo.uminity.common.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<PageResponse<CommentResponseDto>> listComments(
            @PathVariable Long postId,
            @Valid @ModelAttribute CommentListRequest req
    ) {
        PageResponse<CommentResponseDto> page = commentService.listComments(postId, req);
        return ResponseEntity.ok(page);
    }


    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest req,
            @RequestHeader("X-USER-ID") String userId
    ) {
        CommentResponseDto dto = commentService.createComment(postId, req, userId);
        return ResponseEntity
                .status(201)
                .body(dto);
    }
    // 실제로는 @AuthenticationPrincipal 로 교체

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest req,
            @RequestHeader("X-USER-ID") String userId
    ) {
        CommentResponseDto dto = commentService.updateComment(commentId, req, userId);
        return ResponseEntity.ok(dto);
    }


    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestHeader("X-USER-ID") String userId
    ) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}