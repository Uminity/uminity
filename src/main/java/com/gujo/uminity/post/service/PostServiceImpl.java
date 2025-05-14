package com.gujo.uminity.post.service;

import static java.time.LocalDateTime.now;

import com.gujo.uminity.common.PageResponse;
import com.gujo.uminity.post.dto.request.PostCreateRequest;
import com.gujo.uminity.post.dto.request.PostListRequest;
import com.gujo.uminity.post.dto.request.PostUpdateRequest;
import com.gujo.uminity.post.dto.response.PostResponseDto;
import com.gujo.uminity.post.entity.Post;
import com.gujo.uminity.post.repository.PostRepository;
import com.gujo.uminity.user.entity.User;
import com.gujo.uminity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

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
            case USERID:
                postPage = postRepository.findByUser_UserIdOrderByCreatedAtDesc(keyword, pageable);
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
        // Page -> 매핑 -> 페이지리스폰스로 응답
    }

    @Override
    @Transactional
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId).
                orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글"));
        return PostResponseDto.fromEntity(post);
    }

    @Override
    @Transactional
    public PostResponseDto createPost(PostCreateRequest request) {

        // 유저 조회부터 해야됨
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

//        Post post = new Post();
//        post.setTitle(request.getTitle());
//        post.setContent(request.getContent());
//        post.setCreatedAt(LocalDateTime.now());
//        post.setViewCnt(0);

        Post post = Post.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(now())
                .viewCnt(0)
                .build();

        Post saved = postRepository.save(post);
        return PostResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public PostResponseDto updatePost(Long postId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 압ㅎ습니다"));
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        return PostResponseDto.fromEntity(post);
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
}

/*
CRUD 만들고 났고 조회랑 수정은 있는지 여부를 확인해야되고 없으면 예외 던지기
그리고 응답객체에 post 빌더패턴으로 생성해서 넣어주기

JPA 작업을 하나의 트랜잭션으로 묶고 자동으로 롤백해서

연관관계로 UserRepository 추가해서 반영
 */