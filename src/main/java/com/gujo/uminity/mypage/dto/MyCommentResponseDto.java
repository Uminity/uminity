package com.gujo.uminity.mypage.dto;

import com.gujo.uminity.comment.entity.Comment;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyCommentResponseDto {
    // 게시글ID postId
    // 댓글 content
    // 작성날짜 createdAt
    private Long postId;
    private String postTitle;
    private String content;
    private LocalDateTime createdAt;

    public static MyCommentResponseDto fromEntity(Comment c) {
        return MyCommentResponseDto.builder()
                .postId(c.getPost().getPostId())
                .postTitle(c.getPost().getTitle())
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
