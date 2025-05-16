package com.gujo.uminity.like.service;

import com.gujo.uminity.common.web.PageResponse;
import com.gujo.uminity.like.dto.response.CheckLikeResponse;
import com.gujo.uminity.like.dto.response.ToggleLikeResponse;
import com.gujo.uminity.post.dto.response.PostResponseDto;
import org.springframework.data.domain.Pageable;

public interface LikeService {

    ToggleLikeResponse toggleLike(String userId, Long postId);

    PageResponse<String> getLikerNamesByPostId(Long postId, Pageable pageable);

    PageResponse<PostResponseDto> getMyLikedPosts(String userId, Pageable pageable);

    CheckLikeResponse checkLike(String userId, Long postId);
}

// 좋아요 결과 응답 DOT 반환
// 게시글에 좋아요 누른 사용자 이름 목록 조회
// 사용자(내)가 좋아요한 게시글 목록 조회
// DIP 준수
