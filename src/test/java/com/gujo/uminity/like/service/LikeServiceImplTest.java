package com.gujo.uminity.like.service;

import com.gujo.uminity.like.dto.response.ToggleLikeResponse;
import com.gujo.uminity.like.entity.Like;
import com.gujo.uminity.like.repository.LikeRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private LikeServiceImpl likeService;

    private User user;
    private Post post;
    private final String userId = "user1";
    private final Long postId = 1L;

    @BeforeEach
    void 설정() {
        // User 준비
        user = User.builder()
                .userId(userId)
                .name("tester")
                .email("test@test.com")
                .password("pwd")
                .phone("010-1111-1111")
                .build();

        post = Post.of(user, "테스트 제목", "테스트 내용");
        // postId 매핑 (JPA 없이 직접 할당)
        ReflectionTestUtils.setField(post, "postId", postId);
    }

    @Test
    @DisplayName("게시글 미존재 시 IllegalArgumentException 발생")
    void 게시글이없는데좋아요는못누르지() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(postRepository.findById(postId))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> likeService.toggleLike(userId, postId)
        );
        assertEquals("존재하지 않는 게시글: " + postId, ex.getMessage());

        verify(userRepository).findById(userId);
        verify(postRepository).findById(postId);
        verifyNoInteractions(likeRepository);
    }

    @Test
    @DisplayName("이미 좋아요 상태면 flag=false, 좋아요 수 감소")
    void 좋아요취소() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.existsByUserUserIdAndPostPostId(userId, postId))
                .thenReturn(true);
        when(likeRepository.countByPostPostId(postId))
                .thenReturn(4L);

        ToggleLikeResponse response = likeService.toggleLike(userId, postId);

        assertFalse(response.isLikedByMe());
        assertEquals(4L, response.getLikeCount());
        verify(likeRepository).deleteByUserUserIdAndPostPostId(userId, postId);
        verify(likeRepository).countByPostPostId(postId);
    }

    @Test
    @DisplayName("좋아요 상태가 아니면 flag=true, 좋아요 수 증가")
    void 좋아요() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.existsByUserUserIdAndPostPostId(userId, postId))
                .thenReturn(false);
        when(likeRepository.countByPostPostId(postId))
                .thenReturn(1L);

        ToggleLikeResponse response = likeService.toggleLike(userId, postId);

        assertTrue(response.isLikedByMe());
        assertEquals(1L, response.getLikeCount());
        verify(likeRepository).save(any(Like.class));
        verify(likeRepository).countByPostPostId(postId);
    }
}