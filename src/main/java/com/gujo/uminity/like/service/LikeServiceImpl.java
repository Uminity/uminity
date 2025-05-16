package com.gujo.uminity.like.service;

import com.gujo.uminity.common.web.PageResponse;
import com.gujo.uminity.like.dto.response.ToggleLikeResponse;
import com.gujo.uminity.like.entity.Like;
import com.gujo.uminity.like.repository.LikeRepository;
import com.gujo.uminity.post.dto.response.PostResponseDto;
import com.gujo.uminity.post.entity.Post;
import com.gujo.uminity.post.repository.PostRepository;
import com.gujo.uminity.user.entity.User;
import com.gujo.uminity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public ToggleLikeResponse toggleLike(String userId, Long postId) {
        // 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + userId));
        // 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글: " + postId));

        boolean likedByMe;
        if (likeRepository.existsByUserUserIdAndPostPostId(userId, postId)) {
            likeRepository.deleteByUserUserIdAndPostPostId(userId, postId);
            likedByMe = false;
        } else {
            Like like = Like.of(user, post);
            likeRepository.save(like);
            likedByMe = true;
        }

        long count = likeRepository.countByPostPostId(postId);
        return ToggleLikeResponse.builder()
                .likedByMe(likedByMe)
                .likeCount(count)
                .build();
    }

    @Override
    public PageResponse<String> getLikerNamesByPostId(Long postId, Pageable pageable) {
        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글: " + postId));
        // 좋아요 누른 사용자 이름 목록 조회
        Page<String> page = likeRepository.findLikerNamesByPostId(postId, pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public PageResponse<PostResponseDto> getMyLikedPosts(String userId, Pageable pageable) {
        // 내가 좋아요 한 목록 보기
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + userId));
        Page<PostResponseDto> dtoPage = likeRepository.findPostsByUserUserId(userId, pageable)
                .map(PostResponseDto::fromEntity);
        return new PageResponse<>(
                dtoPage.getContent(),
                dtoPage.getNumber(),
                dtoPage.getSize(),
                dtoPage.getTotalElements(),
                dtoPage.getTotalPages()
        );
    }
}