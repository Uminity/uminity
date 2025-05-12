package com.gujo.uminity.post.service;

import com.gujo.uminity.common.PageResponse;
import com.gujo.uminity.post.dto.request.PostListRequest;
import com.gujo.uminity.post.dto.response.PostResponseDto;
import com.gujo.uminity.post.entity.Post;
import com.gujo.uminity.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public PageResponse<PostResponseDto> listPosts(PostListRequest req) {

        // 요청 객체에서 꺼내고
        String keyword = req.getKeyword();
        int page = req.getPage();
        int size = req.getSize();
        PostListRequest.SearchType type = req.getSearchType();

        // 내림차
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 타입확인
        Page<Post> postPage;
        switch (type) {
            case TITLE:
                postPage = postRepository.findByTitleContainingIgnoreCase(keyword, pageable);
                break;
            case CONTENT:
                postPage = postRepository.findByContentContainingIgnoreCase(keyword, pageable);
                break;
            default:
                postPage = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword, pageable);
                break;
        }
        Page<PostResponseDto> dtoPage = postPage.map(PostResponseDto::fromEntity);

        return new PageResponse<>(
                dtoPage.getContent(),
                dtoPage.getNumber(),
                dtoPage.getSize(),
                dtoPage.getTotalElements(),
                dtoPage.getTotalPages()
        );
    }
}