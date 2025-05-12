package com.gujo.uminity.post.controller;

import com.gujo.uminity.common.PageResponse;
import com.gujo.uminity.post.dto.request.PostListRequest;
import com.gujo.uminity.post.dto.response.PostResponseDto;
import com.gujo.uminity.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Validated
public class PostController {
    private final PostService postService;

    // 1. 목록 조회 ( 검색 + 페이지네이션_)
    // 쿼리파리미터를 모델객체로 바인딩 밸리드로 DTO 검증
    @GetMapping
    public ResponseEntity<PageResponse<PostResponseDto>> listPosts(
            @Valid @ModelAttribute PostListRequest req) {
        PageResponse<PostResponseDto> result = postService.listPosts(req);
        return ResponseEntity.ok(result);
    }

    // 2. 단건
//    @GetMapping("/{postId}")
//    public ResponseEntity<PostResponseDto> getPost(@PathVariable("postId") Long postId) {
//    }

    // 3. 게시글 생성
//    @PostMapping


    // 4. 게시그 ㄹ수정
//    @PutMapping

    // 5. 게시글 삭제
}
