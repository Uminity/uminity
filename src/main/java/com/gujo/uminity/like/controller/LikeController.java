package com.gujo.uminity.like.controller;

import com.gujo.uminity.common.security.MyUserDetails;
import com.gujo.uminity.common.web.PageResponse;
import com.gujo.uminity.like.dto.response.ToggleLikeResponse;
import com.gujo.uminity.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{postId}/like")
    public ResponseEntity<ToggleLikeResponse> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal MyUserDetails principal) {
//        ID 추출
        String userId = principal.getUserId();

        ToggleLikeResponse dto = likeService.toggleLike(userId, postId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{postId}/likers")
    public ResponseEntity<PageResponse<String>> getLikerNames(
            @PathVariable Long postId,
            Pageable pageable) {
        PageResponse<String> response = likeService.getLikerNamesByPostId(postId, pageable);
        return ResponseEntity.ok(response);
    }
}

/*
이유·근거:
        * 1. POST /api/posts/{postId}/like: 특정 게시글에 대한 토글 액션 처리
 * 2. GET  /api/posts/{postId}/likers: 게시글별 좋아요 사용자 이름 목록 제공
 * 3. RESTful 설계 원칙 준수 및 관심사 분리
 */


/**
 * 사용자 단위 좋아요 목록 조회 API 컨트롤러
 * <p>
 * 이유·근거:
 * 1. GET /api/users/me/likes: 인증된 사용자의 좋아요한 게시글 목록 조회
 * 2. @AuthenticationPrincipal 활용해 안전하게 사용자 정보 획득
 * 3. 관심사 분리로 컨트롤러 역할 명확화
 */