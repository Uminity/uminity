package com.gujo.uminity.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

public class CommentCreateRequest {
    @NotNull(message = "postId null 아님")
    private Long postId;

    private Long parentId;

    @NotBlank(message = "내용 입력 필요")
    private String Content;
}

/*
post와 comment는 N:1이여서 처음에 댓글 등록할 때 postId가 반드시 있어야 한다.
있어야 게시글에 댓글을 달 수 있고 parentId가 있을경우 대댓글로 인식된다.
 */