package com.gujo.uminity.post.repository;

import com.gujo.uminity.post.entity.Post;
import com.gujo.uminity.user.entity.User;
import com.gujo.uminity.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
// JPA 만 띄워서
class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    private User userTest;

    @BeforeEach
    void setUp() {
        userTest = userRepository.save(User.builder()
                .name("테스트")
                .email("test@test.com")
                .password("password")
                .phone("010-111-1111")
                .build()
        );
        postRepository.saveAll(List.of(
                Post.of(userTest, "제목1", "내용1"),
                Post.of(userTest, "제목2", "내용2"),
                Post.of(userTest, "제목3", "내용3")

        ));
    }

    @Test
    @DisplayName("저장하고 조회까지")
    void 저장조회() {
        Post p = Post.of(userTest, "테스트 제목", "테스트 내용");
        Post saved = postRepository.save(p);

        Post found = postRepository.findById(saved.getPostId())
                .orElseThrow(() -> new AssertionError("조회안됨"));
        assertThat(found.getTitle()).isEqualTo("테스트 제목");
        assertThat(found.getContent()).isEqualTo("테스트 내용");
    }

    @Test
    @DisplayName("제목 포함 검색, 페이징이 정상")
    void 제목() {

        Pageable pageable = PageRequest.of(0, 2, Sort.by("createdAt").descending());

        Page<Post> page = postRepository.findByTitleContainingIgnoreCase("제목", pageable);

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent()).allMatch(p -> p.getTitle().contains("제목"));
        assertThat(page.getTotalPages()).isEqualTo(2);

        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("제목 내용 포함 검색, 페이징이 정상")
    void 제목내용() {

        Pageable pageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());

        Page<Post> page = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase("제목", "내용", pageable);

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(1);

        assertThat(page.getContent()).hasSize(3);
    }
}

/*
 trouble
 JPA만 띄워서 테스트하려햇는데 내장 디비가 있어야 된다고 하네 레포지토리 메서드 동작하는지 확인하기 위해서 H2 디비 써야되겠는데,

 trouble shooting
 test의존성 추가 해서 DB를 따로 쓸 수 있게 되었다.

 어설트 : 3개 매칭 , 모든 포스트에 제목 포함, 페이지 수는 2개, 현재 페이지에는 2개
 */