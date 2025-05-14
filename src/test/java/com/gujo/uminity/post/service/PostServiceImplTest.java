package com.gujo.uminity.post.service;

import com.gujo.uminity.common.PageResponse;
import com.gujo.uminity.post.dto.request.PostCreateRequest;
import com.gujo.uminity.post.dto.request.PostListRequest;
import com.gujo.uminity.post.dto.request.PostUpdateRequest;
import com.gujo.uminity.post.dto.response.PostResponseDto;
import com.gujo.uminity.post.entity.Post;
import com.gujo.uminity.post.repository.PostRepository;
import com.gujo.uminity.user.entity.User;
import com.gujo.uminity.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;
    // 가짜 수집

    @InjectMocks
    private PostServiceImpl postService;


    private User userTest;
    private Post postTest;

    @BeforeEach
    void 설정() {
        // 작성자
        userTest = User.builder()
                .userId(String.valueOf(UUID.randomUUID()))
                .name("테스트 유저")
                .email("test@tset.com")
                .password("password")
                .phone("010-1111-1111")
                .build();
        postTest = Post.builder()
                .postId(1L)
                .user(userTest)
                .title("타이틀 제목")
                .content("내용")
                .createdAt(now())
                .viewCnt(0)
                .build();
    }

    @Test
    @DisplayName("엔티티를 DTO로 반환")
    void 게시글생성성공() {
        // given
        PostCreateRequest req = new PostCreateRequest();
        req.setTitle("제목");
        req.setContent("내용");

        given(userRepository.findById(userTest.getUserId()))
                .willReturn(Optional.of(userTest));

        given(postRepository.save(any(Post.class)))
                .willReturn(postTest);
        // when
        PostResponseDto dto = postService.createPost(req, userTest.getUserId());

        // then
        assertThat(dto.getPostId()).isEqualTo(postTest.getPostId());

        assertThat(dto.getAuthorName()).isEqualTo(postTest.getUser().getName());
        assertThat(dto.getAuthorName()).isEqualTo(userTest.getName());
    }
    /*
    서비스 -> 레포지토리 -> DTO 매핑 순서
     */

    @Test
    @DisplayName("존재하는 ID면 DTO 반환")
    void 게시글조회성공() {
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

    @Test
    @DisplayName("listPosts: SEARCH_TYPE=ALL알 때 검색 호출")
    void listPosts_allSearch() {
        // Given: ALL 검색일 때 두 필드 OR 검색
        PostListRequest req = new PostListRequest();
        req.setKeyword("키워드");
        req.setPage(0);
        req.setSize(10);
        req.setSearchType(PostListRequest.SearchType.ALL);

        Page<Post> stubPage = new PageImpl<>(
                List.of(postTest),
                PageRequest.of(0, 10, Sort.by("createdAt").descending()),
                1
        );
        given(postRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                        eq("키워드"), eq("키워드"), any(Pageable.class)))
                .willReturn(stubPage);

        // When
        PageResponse<PostResponseDto> page = postService.listPosts(req);

        // Then: totalElements, DTO 매핑 검증
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getPostId())
                .isEqualTo(postTest.getPostId());
    }

}