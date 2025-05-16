package com.gujo.uminity.post.service;

import com.gujo.uminity.common.web.PageResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public PostResponseDto createPost(PostCreateRequest request, String userId) {

        // 유저 조회부터 해야됨
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

//        Post post = new Post();
//        post.setTitle(request.getTitle());
//        post.setContent(request.getContent());
//        post.setCreatedAt(LocalDateTime.now());
//        post.setViewCnt(0);

        Post post = Post.of(user, request.getTitle(), request.getContent());
        Post saved = postRepository.save(post);
        return PostResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public PostResponseDto updatePost(Long postId, PostUpdateRequest request, String userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 게시글: " + postId));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인만 수정할 수 있습니다.");
        }

        post.updateTitle(request.getTitle());
        post.updateContent(request.getContent());

        return PostResponseDto.fromEntity(post);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 게시글: " + postId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isManager = false;
        if (auth != null) {
            isManager = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
        }

        if (!post.getUser().getUserId().equals(userId) && !isManager) {
            throw new IllegalArgumentException("본인만 삭제할 수 있습니다.");
        }
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void incrementViewCountIfNew(Long postId, boolean isNew) {
        if (!isNew) {
            return; // 이미 본 게시글이면 패스
        }

        try {
            int updated = postRepository.incrementViewCount(postId);
            // 조회수 증가 실패해도 조회는 가능해야 하므로 여기서 예외 X
            if (updated == 0) {
                System.out.println("[INFO] 조회수 증가 실패: 존재하지 않는 게시글 postId=" + postId);
                // 증가 실패는 로깅만 하고 넘긴다
            }
        } catch (Exception e) {
            // 증가 중 문제가 생겨도 조회는 가능해야 함
            System.out.println("[ERROR] 조회수 증가 중 예외 발생: " + e.getMessage());
        }
    }
}
