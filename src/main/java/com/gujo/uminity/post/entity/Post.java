package com.gujo.uminity.post.entity;

import com.gujo.uminity.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @NotBlank(message = "제목은 비어있을 수 없습니다.")
    private String title;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "내용은 비어있을 수 없습니다.")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "view_cnt", nullable = false)
    private Integer viewCnt;

    public static Post of(User user, String title, String content) {
        return Post.builder()
                .user(user)
                .title(title)
                .content(content)
                .createdAt(LocalDateTime.now())
                .viewCnt(0)
                .build();
    }

    public void updateTitle(String newTitle) {
        this.title = newTitle;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public void increaseViewCount() {
        this.viewCnt++;
    }
}


/*
연관관계를 위해서 UUID 필드 삭제하고 User와 다대일 관계 설정
fetch LAZY 실제로 사용할 때만 유저조회

Setter 제거 불변성 유지, 의도치 않은 변경 방지
@Builder + 정적 팩토리 메서드로 생성 책임 집중
검증 로직은 Bean Validation(@NotBlank) 에만 맡기도록
 */