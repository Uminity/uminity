package com.gujo.uminity.like.repository;

import com.gujo.uminity.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserUserIdAndPostPostId(String userId, Long postId);

    void deleteByUserUserIdAndPostPostId(String userId, Long postId);

    long countByPostPostId(Long postId);
}
