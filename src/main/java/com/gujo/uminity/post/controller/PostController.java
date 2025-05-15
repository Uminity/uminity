package com.gujo.uminity.post.controller;

import com.gujo.uminity.common.web.PageResponse;
import com.gujo.uminity.common.security.MyUserDetails;
import com.gujo.uminity.post.dto.request.PostCreateRequest;
import com.gujo.uminity.post.dto.request.PostListRequest;
import com.gujo.uminity.post.dto.request.PostUpdateRequest;
import com.gujo.uminity.post.dto.response.PostResponseDto;
import com.gujo.uminity.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    // post 조회만 하는 컨트롤러, 조회수 관리만 하는 서비스, 쿠키를 통해 검증여부는 인터셉터가

    // 3. 게시글 생성
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @Valid @RequestBody PostCreateRequest req,
            @AuthenticationPrincipal MyUserDetails principal) {

        String userId = principal.getUserId();
        PostResponseDto created = postService.createPost(req, userId);
//        PostResponseDto created = postService.createPost(req);
        return ResponseEntity
                .created(URI.create("/api/posts/" + created.getPostId()))
                .body(created);
    }
    /*
    HTTP 요청 본문에 사용자 ID를 담지 않고, 시큐리티 컨텍스트로부터 꺼내오도록 해야 위·변조를 방지할 수 있다!!!!!!!!!!!!
     */

    // 4. 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable("postId") Long postId, @Valid @RequestBody PostUpdateRequest req,
            @AuthenticationPrincipal MyUserDetails principal) {

        String userId = principal.getUserId();

        PostResponseDto updated = postService.updatePost(postId, req, userId);
        return ResponseEntity.ok(updated);
    }

    // 5. 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable("postId") Long postId, @AuthenticationPrincipal MyUserDetails principal) {
        postService.deletePost(postId, principal.getUserId());
        return ResponseEntity.noContent().build();
    }
}
