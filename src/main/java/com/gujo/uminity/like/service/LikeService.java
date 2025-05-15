package com.gujo.uminity.like.service;

import com.gujo.uminity.like.dto.response.ToggleLikeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikeService {

    ToggleLikeResponse toggleLike(String userId, Long postId);

    Page<String> getLikerNamesByPostId(Long postId, Pageable pageable);
}

// 좋아요 결과 응답 DOT 반환
// DIP 준수
