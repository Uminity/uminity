package com.gujo.uminity.like.entity;

import com.gujo.uminity.post.entity.Post;
import com.gujo.uminity.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)

public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likes_id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public static Like of(User user, Post post) {
        return Like.builder()
                .user(user)
                .post(post)
                .build();
    }
}

/*
도메인 무결성 , 엔티티는 세터가 되면 안된다 왜냐면 불변성을 가지고 있어야 되기 때문에
JPA 는 NoArgs 할 때 기본 생성자가 public 해서 new 엔티티가 사용가능해져서
정적 팩토리 메소드로만 호출 할 수 있게 접근 제어를 해야된다.
-> of 정적 팩토리 메소드로 사용자나 게시글 누락을 막는다.
 */