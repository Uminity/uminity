package com.gujo.uminity.post.service;

import com.gujo.uminity.common.PageResponse;
import com.gujo.uminity.post.dto.request.PostCreateRequest;
import com.gujo.uminity.post.dto.request.PostListRequest;
import com.gujo.uminity.post.dto.request.PostUpdateRequest;
import com.gujo.uminity.post.dto.response.PostResponseDto;

public interface PostService {
    PageResponse<PostResponseDto> listPosts(PostListRequest req);

    PostResponseDto getPost(Long postId);

    PostResponseDto createPost(PostCreateRequest request, String userId);

    PostResponseDto updatePost(Long postId, PostUpdateRequest request);

    void deletePost(Long postId);
}

/*
 DIP 의존해서 컨트롤러가 구현체가 아니라 역할에만 의존하도록 캐싱 처리
 */