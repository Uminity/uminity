package com.gujo.uminity.comment.service;

import com.gujo.uminity.comment.dto.request.CommentCreateRequest;
import com.gujo.uminity.comment.dto.request.CommentListRequest;
import com.gujo.uminity.comment.dto.request.CommentUpdateRequest;
import com.gujo.uminity.comment.dto.response.CommentResponseDto;
import com.gujo.uminity.common.PageResponse;

public interface CommentService {
    PageResponse<CommentResponseDto> listComments(Long postId, CommentListRequest req);
    // 댓글 목록 조회

    CommentResponseDto createComment(Long postId, CommentCreateRequest req, String userId);

    CommentResponseDto updateComment(Long postId, Long commentId, CommentUpdateRequest req, String userId);

    void deleteComment(Long postId, Long commentId, String userId);

}

/*
userId 인증 사용자 정보를 서비스 계층에서 받는다.
URI로 넘어온 postId 또한 게시글ID와 댓글소속의 ID검증할 수 잇게
 */