package com.gujo.uminity.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChildCommentDto {
    private Long commentId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
}

/*
대 댓글에는 id 와 작성자, 내용, 생성날짜 응답한다.
3개만 보여질 대댓글이다.
 */