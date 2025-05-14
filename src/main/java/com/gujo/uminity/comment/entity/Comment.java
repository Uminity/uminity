package com.gujo.uminity.comment.entity;

import com.gujo.uminity.post.entity.Post;
import com.gujo.uminity.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void updateContent(String newContent) {
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("댓글 내용은 비어 있을 수 없습니다.");
        }
        this.content = newContent;
    }
}

/*
배운 정보
자기 참조 트리 = 부모 자식 간의 관계로 부모에서 분리된 자식은 바로 삭제
parent 필드가 null 이면 최상위 댓글이고 답글은 parent가 가리키는 댓글의 children 리스트에 추가한다.
내용과 날짜는 필수값

어떤 게시글의 댓글인지, 어떤 사용자인지, 부모댓글과 대댓글 구현

 */

