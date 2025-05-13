package com.gujo.uminity.post.controller;

import com.gujo.uminity.common.PageResponse;
import com.gujo.uminity.post.dto.request.PostCreateRequest;
import com.gujo.uminity.post.dto.request.PostListRequest;
import com.gujo.uminity.post.dto.request.PostUpdateRequest;
import com.gujo.uminity.post.dto.response.PostResponseDto;
import com.gujo.uminity.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
        PageResponse<PostResponseDto> page = postService.listPosts(req);
        return ResponseEntity.ok(page);
//        return ResponseEntity.ok().body(page);
    }

    // 2. 단건
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(
            @PathVariable("postId") Long postId) {
        PostResponseDto dto = postService.getPost(postId);
        return ResponseEntity.ok(dto);
    }

    // 3. 게시글 생성
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @Valid @RequestBody PostCreateRequest req) {
        PostResponseDto created = postService.createPost(req);
        return ResponseEntity
                .created(URI.create("/api/posts/" + created.getPostId()))
                .body(created);
    }

    // 4. 게시그 ㄹ수정
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long postId, @Valid @RequestBody PostUpdateRequest req) {
        PostResponseDto updated = postService.updatePost(postId, req);
        return ResponseEntity.ok(updated);
    }

    // 5. 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
