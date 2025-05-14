package com.gujo.uminity.comment.repository;

import com.gujo.uminity.comment.entity.Comment;
import com.gujo.uminity.post.entity.Post;
import com.gujo.uminity.post.repository.PostRepository;
import com.gujo.uminity.user.entity.User;
import com.gujo.uminity.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Post post;
    private Comment parent1, parent2, child11, child12, child21;

    @BeforeEach
    void 초기화() {
        user = userRepository.save(
                User.builder()
                        .name("테스트유저")
                        .email("a@b.com")
                        .password("password")
                        .phone("010-1111-1111")
                        .build()
        );

        post = postRepository.save(
                Post.builder()
                        .user(user)
                        .title("제목")
                        .content("내용")
                        .createdAt(now())
                        .viewCnt(0)
                        .build()
        );

        parent1 = commentRepository.save(
                Comment.builder()
                        .post(post)
                        .user(user)
                        .content("댓글1")
                        .createdAt(now().minusSeconds(60))
                        .build()
        );

        parent2 = commentRepository.save(
                Comment.builder()
                        .post(post)
                        .user(user)
                        .content("댓글2")
                        .createdAt(now().minusSeconds(50))
                        .build()
        );

        child11 = commentRepository.save(
                Comment.builder()
                        .post(post).user(user)
                        .parent(parent1)
                        .content("자식댓글1-1")
                        .createdAt(now().minusSeconds(50))
                        .build()
        );
        child12 = commentRepository.save(
                Comment.builder()
                        .post(post).user(user)
                        .parent(parent1)
                        .content("자식댓글1-2")
                        .createdAt(now().minusSeconds(40))
                        .build()
        );

        child21 = commentRepository.save(
                Comment.builder()
                        .post(post).user(user)
                        .parent(parent2)
                        .content("자식댓글2-1")
                        .createdAt(now().minusSeconds(30))
                        .build()
        );
    }

    @Test
    @DisplayName("부모 댓글만 조회하면 parent1, parent2 2개 반환")
    void 부모댓글조회() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Comment> parents = commentRepository
                .findByPost_PostIdAndParentIsNull(post.getPostId(), pageable)
                .getContent();

        assertThat(parents).containsExactlyInAnyOrder(parent1, parent2);
    }

    @Test
    @DisplayName("parent1 의 최신 자식 댓글 최대 3개 조회")
    void 부모1자식댓글3개씩만() {
        List<Comment> topChildren = commentRepository
                .findTop3ByParent_CommentIdOrderByCreatedAtDesc(parent1.getCommentId());

        assertThat(topChildren).hasSize(2)
                .containsExactly(child12, child11);
    }

    @Test
    @DisplayName("parent2 의 최신 자식 댓글 최대 3개 조회")
    void 부모2자식댓글3개씩만() {
        List<Comment> topChildren = commentRepository
                .findTop3ByParent_CommentIdOrderByCreatedAtDesc(parent2.getCommentId());

        // parent1 에서는 2개만 있으므로 size=2, 최신순 확인
        assertThat(topChildren).hasSize(1)
                .first().isEqualTo(child21);
    }

    @Test
    @DisplayName("parent1 의 자식 댓글 총 개수 집계")
    void 총자식댓글수() {
        long count = commentRepository.countByParent_CommentId(parent1.getCommentId());
        assertThat(count).isEqualTo(2);
    }
}