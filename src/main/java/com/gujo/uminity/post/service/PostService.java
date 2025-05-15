package com.gujo.uminity.post.service;

import com.gujo.uminity.common.web.PageResponse;
import com.gujo.uminity.post.dto.request.PostCreateRequest;
import com.gujo.uminity.post.dto.request.PostListRequest;
import com.gujo.uminity.post.dto.request.PostUpdateRequest;
import com.gujo.uminity.post.dto.response.PostResponseDto;

public interface PostService {
    PageResponse<PostResponseDto> listPosts(PostListRequest req);

    PostResponseDto getPost(Long postId);

    PostResponseDto createPost(PostCreateRequest request, String userId);

    PostResponseDto updatePost(Long postId, PostUpdateRequest request, String userId);

    void deletePost(Long postId, String userId);

    /*
    DIP 의존해서 컨트롤러가 구현체가 아니라 역할에만 의존하도록 캐싱 처리

    인증된 사용자만 자신의 글을 생성 수정 삭제할 수 있게 하려면
    서비스 계층에서 userId를 받아와야한다.
    */

    void incrementViewCount(Long postId);
    /* 조회수는 누가봤는지 검증안해도되고 postId만 검증
    stateless 한 쿠키로 중복 카운팅 방지 - 세션 방식과 쿠키 방식중

    역할 분리해서 HTTP 쿠키 처리는 컨트롤러가 담당하고 서비스는 조회수 증가 로직만 수행하자!!
     */
}

