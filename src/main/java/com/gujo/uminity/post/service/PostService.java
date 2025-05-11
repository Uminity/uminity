package com.gujo.uminity.post.service;

import com.gujo.uminity.common.PageResponse;
import com.gujo.uminity.post.dto.response.PostResponseDto;

public interface PostService {
    PageResponse<PostResponseDto> listPosts(int page, int size);
}

/*
 DIP 의존해서 컨트롤러가 구현체가 아니라 역할에만 의존하도록 캐싱 처리
 */