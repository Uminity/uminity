package com.gujo.uminity.like.controller;

import com.gujo.uminity.common.security.MyUserDetails;
import com.gujo.uminity.like.dto.response.ToggleLikeResponse;
import com.gujo.uminity.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}