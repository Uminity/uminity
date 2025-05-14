package com.gujo.uminity.comment.dto.response;

import com.gujo.uminity.comment.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CommentResponseDto {
    private Long commentId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;

    private int totalChildCount;
    private List<ChildCommentDto> children;

    public static CommentResponseDto fromEntity(
            Comment parent, List<ChildCommentDto> children, long totalChildrenCount) {
        return CommentResponseDto.builder()
                .commentId(parent.getCommentId())
                .userName(parent.getUser().getName())
                .content(parent.getContent())
                .createdAt(parent.getCreatedAt())
                .totalChildCount((int) totalChildrenCount)
                .children(children)
                .build();
    }
}

/*
댓글 전체 응답 -> 부모댓글에다가 총 대댓글 수 + ChildDto 리스트
엔티티랑 DTO 매핑하는건데 부모댓글 자식댓글 DTO

응답 DTO 불변 설계로 Getter랑 Builder만
*/