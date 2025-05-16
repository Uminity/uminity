package com.gujo.uminity.post.service;

import com.gujo.uminity.common.web.PageResponse;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private User userTest;
    private Post postTest;

    @BeforeEach
    void 설정() {
        userTest = User.builder()
                .userId(UUID.randomUUID().toString())
                .name("테스트 유저")
                .email("test@test.com")
                .password("password")
                .phone("010-1111-1111")
                .build();
        postTest = Post.of(userTest, "타이틀 제목", "내용");
    }

    @Test
    @DisplayName("게시글 생성 성공: DTO 반환 검증")
    void 게시글생성성공() {
        PostCreateRequest req = new PostCreateRequest();
        req.setTitle("제목");
        req.setContent("내용");

        given(userRepository.findById(userTest.getUserId()))
                .willReturn(Optional.of(userTest));
        given(postRepository.save(any(Post.class)))
                .willReturn(postTest);

        PostResponseDto dto = postService.createPost(req, userTest.getUserId());

        assertThat(dto.getPostId()).isEqualTo(postTest.getPostId());
        assertThat(dto.getAuthorName()).isEqualTo(userTest.getName());
    }

    @Test
    @DisplayName("게시글 조회 성공: 존재하는 ID")
    void 게시글조회성공() {
        given(postRepository.findById(1L))
                .willReturn(Optional.of(postTest));

        PostResponseDto dto = postService.getPost(1L);

        assertThat(dto.getPostId()).isEqualTo(postTest.getPostId());
        assertThat(dto.getTitle()).isEqualTo(postTest.getTitle());
    }

    @Test
    @DisplayName("게시글 조회 실패: 없는 ID 예외 발생")
    void 게시글조회실패() {
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPost(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 게시글");
    }

    @Test
    @DisplayName("게시글 수정 성공: 제목/내용 변경")
    void 게시글수정성공() {
        given(postRepository.findById(1L))
                .willReturn(Optional.of(postTest));

        PostUpdateRequest req = new PostUpdateRequest();
        req.setTitle("수정된제목");
        req.setContent("수정된내용");

        PostResponseDto dto = postService.updatePost(1L, req, userTest.getUserId());

        assertThat(dto.getTitle()).isEqualTo("수정된제목");
        assertThat(dto.getContent()).isEqualTo("수정된내용");
    }

    @Test
    @DisplayName("게시글 수정 실패: 다른 사용자 권한 예외 발생")
    void 게시글수정실패() {
        given(postRepository.findById(1L))
                .willReturn(Optional.of(postTest));

        PostUpdateRequest req = new PostUpdateRequest();
        req.setTitle("타이틀");
        req.setContent("내용");

        assertThatThrownBy(() -> postService.updatePost(1L, req, "other-id"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인만 수정할 수 있습니다.");
    }

    @Test
    @DisplayName("게시글 삭제 성공: 작성자 권한")
    void 게시글삭제성공() {
        given(postRepository.findById(1L))
                .willReturn(Optional.of(postTest));

        postService.deletePost(1L, userTest.getUserId());

        then(postRepository).should().delete(postTest);
    }

    @Test
    @DisplayName("게시글 삭제 실패: 다른 사용자 권한 예외 발생")
    void 게시글삭제실패() {
        given(postRepository.findById(1L))
                .willReturn(Optional.of(postTest));

        assertThatThrownBy(() -> postService.deletePost(1L, "another-id"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인만 삭제할 수 있습니다.");
    }

    @Test
    @DisplayName("게시글 삭제 실패: 없는 ID 예외 발생")
    void 게시글삭제실패_없음() {
        given(postRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> postService.deletePost(999L, userTest.getUserId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재 하지 않는 게시글: " + 999L);
    }

    @Test
    @DisplayName("모든 게시글 조회: SEARCH_TYPE=ALL 호출 검증")
    void 모든게시글조회() {
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
        given(postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                eq("키워드"), eq("키워드"), any(Pageable.class)
        )).willReturn(stubPage);

        PageResponse<PostResponseDto> page = postService.listPosts(req);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getPostId()).isEqualTo(postTest.getPostId());
    }

    @Test
    @DisplayName("isNew=true일 때 조회수 증가 호출")
    void 조회수증가_신규호출() {
        Long postId = 42L;
        given(postRepository.incrementViewCount(postId)).willReturn(1);

        postService.incrementViewCountIfNew(postId, true);

        then(postRepository).should().incrementViewCount(postId);
    }

    @Test
    @DisplayName("isNew=false일 때 조회수 증가 미호출")
    void 조회수증가_미호출() {
        Long postId = 42L;

        postService.incrementViewCountIfNew(postId, false);

        then(postRepository).should(never()).incrementViewCount(anyLong());
    }

    @Test
    @DisplayName("isNew=true이고 존재하지 않는 게시글이면 예외 발생")
    void 조회수증가_예외() {
        Long postId = 42L;
        given(postRepository.incrementViewCount(postId)).willReturn(0);

        assertThatThrownBy(() -> postService.incrementViewCountIfNew(postId, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 게시글: " + postId);
    }
}
