package com.gujo.uminity.like.service;

import com.gujo.uminity.like.dto.response.ToggleLikeResponse;
import com.gujo.uminity.like.entity.Like;
import com.gujo.uminity.like.repository.LikeRepository;
import com.gujo.uminity.post.entity.Post;
import com.gujo.uminity.post.repository.PostRepository;
import com.gujo.uminity.user.entity.User;
import com.gujo.uminity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
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
}