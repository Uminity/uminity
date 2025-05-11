package com.gujo.uminity.post.service;

import com.gujo.uminity.common.PageResponse;
import com.gujo.uminity.post.dto.request.PostListRequest;
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
import org.springframework.data.domain.*;

import java.util.List;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;
    // 가짜 수집

    @InjectMocks
    private PostServiceImpl postService;

    private Pageable pageable;

    @BeforeEach
    void 설정() {
        pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
    }

    @Test
    @DisplayName("DTO 매핑 검증 + 제목 or 내용")
    void ALL() {
        // given
        Post p1 = new Post(1L, UUID.randomUUID(), "제목1", "내용1", now(), 0);
        Post p2 = new Post(2L, UUID.randomUUID(), "제목2", "내용2", now(), 4);
        Page<Post> fake = new PageImpl<>(List.of(p1, p2), pageable, 2);

        given(postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                "test", "test", pageable))
                .willReturn(fake);

        PostListRequest req = new PostListRequest();
        req.setKeyword("test");
        req.setSearchType(PostListRequest.SearchType.ALL);
        req.setPage(0);
        req.setSize(10);

        // when
        PageResponse<PostResponseDto> response = postService.listPosts(req);

        // then
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getContent()).extracting(PostResponseDto::getPostId)
                .containsExactly(1L, 2L);
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(10);

        then(postRepository).should(times(1))
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase("test", "test", pageable);
    }
}