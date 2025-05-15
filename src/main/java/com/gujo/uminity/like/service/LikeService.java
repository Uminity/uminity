package com.gujo.uminity.like.service;

import com.gujo.uminity.like.dto.response.ToggleLikeResponse;

public interface LikeService {

    ToggleLikeResponse toggleLike(String userId, Long postId);
}

// 좋아요 결과 응답 DOT 반환
// DIP 준수
