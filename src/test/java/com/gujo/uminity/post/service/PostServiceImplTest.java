package com.gujo.uminity.post.service;

import com.gujo.uminity.post.dto.request.PostCreateRequest;
import com.gujo.uminity.post.dto.request.PostUpdateRequest;
import com.gujo.uminity.post.dto.response.PostResponseDto;
import com.gujo.uminity.post.entity.Post;
import com.gujo.uminity.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;
    // 가짜 수집

    @InjectMocks
    private PostServiceImpl postService;

    private UUID userId;
    private Post postTest;

    @BeforeEach
    void 설정() {
        userId = UUID.randomUUID();
        postTest = new Post(1L, userId,
                "테스트 제목,", "테스트 내용", now(), 0);
    }

    @Test
    @DisplayName("createPost: 저장된 엔티티를 DTO로 반환")
    void createPost_success() {
        // given
        PostCreateRequest req = new PostCreateRequest();
        req.setTitle("제목");
        req.setContent("내용");

        given(postRepository.save(any(Post.class)))
                .willReturn(postTest);
        // when
        PostResponseDto dto = postService.createPost(req);

        // then
        assertThat(dto.getPostId()).isEqualTo(postTest.getPostId());
    }
    /*
    서비스 -> 레포지토리 -> DTO 매핑 순서
     */

    @Test
    @DisplayName("getPost: 존재하는 ID면 DTO 반환")
    void getPost_success() {
        // given: findById(1L) 이 postTest 를 반환
        given(postRepository.findById(1L))
                .willReturn(Optional.of(postTest));

        // when: 서비스 호출
        PostResponseDto dto = postService.getPost(1L);

        // then: DTO 필드가 postTest 와 일치
        assertThat(dto.getPostId()).isEqualTo(postTest.getPostId());
        assertThat(dto.getTitle()).isEqualTo(postTest.getTitle());
    }

    @Test
    @DisplayName("getPost: 없는 ID면 IllegalArgumentException 발생")
    void getPost_notFound() {
        // given: findById(any) 이 빈 Optional
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // When / Then: 예외 던짐
        assertThatThrownBy(() -> postService.getPost(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 게시글");
    }

    @Test
    @DisplayName("updatePost: 존재하는 ID면 title/content 변경 후 DTO 반환")
    void updatePost_success() {
        // given: 수정 대상 엔티티 조회
        given(postRepository.findById(1L))
                .willReturn(Optional.of(postTest));

        PostUpdateRequest req = new PostUpdateRequest();
        req.setTitle("수정된제목");
        req.setContent("수정된내용");

        // when
        PostResponseDto dto = postService.updatePost(1L, req);

        // then 엔티티가 변경되어 DTO에 반영되었는지
        assertThat(dto.getTitle()).isEqualTo("수정된제목");
        assertThat(dto.getContent()).isEqualTo("수정된내용");
    }

    @Test
    @DisplayName("updatePost: 없는 ID면 IllegalArgumentException 발생")
    void updatePost_notFound() {
        // given findById → 빈 Optional
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> postService.updatePost(123L, new PostUpdateRequest()))
                .isInstanceOf(IllegalArgumentException.class);
    }

}