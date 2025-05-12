package com.gujo.uminity.post.entity;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "post_likes")
@IdClass(PostLike.PostLikeId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PostLike {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Id
    @Column(name = "post_id")
    private Long postId;

    /**
     * IdClass용 복합키 정의
     */
    public static class PostLikeId implements Serializable {
        private UUID userId;
        private Long postId;

        public PostLikeId() {
        }

        public PostLikeId(UUID userId, Long postId) {
            this.userId = userId;
            this.postId = postId;
        }
    }
}
