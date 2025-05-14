package com.gujo.uminity.comment.service;

import com.gujo.uminity.comment.dto.request.CommentCreateRequest;
import com.gujo.uminity.comment.dto.request.CommentListRequest;
import com.gujo.uminity.comment.dto.request.CommentUpdateRequest;
import com.gujo.uminity.comment.dto.response.CommentResponseDto;
import com.gujo.uminity.comment.entity.Comment;
import com.gujo.uminity.comment.repository.CommentRepository;
import com.gujo.uminity.common.PageResponse;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user1, user2;
    private Post post;
    private Comment parent;

    @BeforeEach
    void 설정() {
//        2명의 유저
        user1 = User.builder()
                .userId("id1")
                .name("User1")
                .email("a@a.a")
                .password("pass")
                .phone("010-1111-1111")
                .build();

        user2 = User.builder()
                .userId("id2")
                .name("User2")
                .email("a@a.a2")
                .password("pass2")
                .phone("010-1111-1112")
                .build();

//        게시글 1개
        post = Post.builder()
                .postId(15L)
                .user(user1)
                .title("Title")
                .content("content")
                .createdAt(now())
                .viewCnt(0)
                .build();
//      부모 댓글
        parent = Comment.builder()
                .commentId(100L)
                .post(post)
                .user(user1)
                .content("댓글")
                .createdAt(now())
                .build();
    }

    @Test
    @DisplayName("댓글 목록 불러올 때 게시글 없으면 예외")
    void 게시글없음() {
        given(postRepository.findById(10L)).willReturn(Optional.empty());

        assertThatThrownBy(() ->
                commentService.listComments(10L, new CommentListRequest())
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 게시글");
    }

    @Test
    @DisplayName("listComments: DTO 매핑")
    void 게시글있음() {
        given(postRepository.findById(10L)).willReturn(Optional.of(post));

        given(commentRepository.findByPost_PostIdAndParentIsNull(
                eq(10L), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(parent), PageRequest.of(0, 5), 1));

        given(commentRepository.findTop3ByParent_CommentIdOrderByCreatedAtDesc(100L))
                .willReturn(List.of());
        given(commentRepository.countByParent_CommentId(100L)).willReturn(0L);

        PageResponse<CommentResponseDto> page = commentService.listComments(10L, new CommentListRequest());

        assertThat(page.getTotalElements()).isEqualTo(1);
        CommentResponseDto dto = page.getContent().get(0);

        assertThat(dto.getCommentId()).isEqualTo(100L);
        assertThat(dto.getChildren()).isEmpty();
        assertThat(dto.getTotalChildCount()).isZero();
    }

    @Test
    @DisplayName("댓글 저장 후 DTO 반환")
    void 댓글저장성공() {

        CommentCreateRequest req = new CommentCreateRequest();
        req.setContent("test");

        given(userRepository.findById("id1")).willReturn(Optional.of(user1));
        given(postRepository.findById(post.getPostId())).willReturn(Optional.of(post));

        Comment toSave = Comment.builder()
                .post(post)
                .user(user1)
                .content(req.getContent())
                .createdAt(now())
                .build();

        Comment saved = Comment.builder()
                .commentId(200L)
                .post(post)
                .user(user1)
                .content(req.getContent())
                .createdAt(now())
                .build();

        given(commentRepository.save(any(Comment.class))).willReturn(saved);

        CommentResponseDto dto = commentService.createComment(post.getPostId(), req, "id1");

        assertThat(dto.getCommentId()).isEqualTo(200L);
        assertThat(dto.getContent()).isEqualTo("test");
    }

    @Test
    @DisplayName("비작성자 수정 시 예외")
    void 권한없는댓글수정() {
        given(commentRepository.findById(100L)).willReturn(Optional.of(parent));

        CommentUpdateRequest req = new CommentUpdateRequest();
        req.setContent("upd");

        assertThatThrownBy(() ->
                commentService.updateComment(100L, req, "id2")
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인만 수정할 수 있습니다.");
    }

    @Test
    @DisplayName("댓글이 없으면 삭제 실패")
    void 댓글이없어() {

        given(commentRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() ->
                commentService.deleteComment(999L, "id1")
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 댓글");
    }

    @Test
    @DisplayName("비작성자 삭제 실패")
    void 작성자가아니면실패() {
        given(commentRepository.findById(100L)).willReturn(Optional.of(parent));

        assertThatThrownBy(() -> commentService.deleteComment(100L, user2.getUserId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("본인만 삭제할 수 있습니다.");
    }

    @Test
    @DisplayName("작성자면 정상 삭제")
    void 작성자라서삭제성공() {
        given(commentRepository.findById(100L)).willReturn(Optional.of(parent));

        commentService.deleteComment(100L, user1.getUserId());

        then(commentRepository).should().delete(parent);
    }
}