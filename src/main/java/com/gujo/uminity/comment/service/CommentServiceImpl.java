package com.gujo.uminity.comment.service;

import com.gujo.uminity.comment.dto.request.CommentCreateRequest;
import com.gujo.uminity.comment.dto.request.CommentListRequest;
import com.gujo.uminity.comment.dto.request.CommentUpdateRequest;
import com.gujo.uminity.comment.dto.response.ChildCommentDto;
import com.gujo.uminity.comment.dto.response.CommentResponseDto;
import com.gujo.uminity.comment.entity.Comment;
import com.gujo.uminity.comment.repository.CommentRepository;
import com.gujo.uminity.common.PageResponse;
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

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    @Override
    public PageResponse<CommentResponseDto> listComments(Long postId, CommentListRequest req) {

        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글: " + postId));

        Pageable pageable = PageRequest.of(
                req.getPage(),
                req.getSize(),
                Sort.by("createdAt").ascending()
        );

        Page<Comment> parentPage = commentRepository
                .findByPost_PostIdAndParentIsNull(postId, pageable);

        List<CommentResponseDto> dtoList = new ArrayList<>();
        for (Comment parent : parentPage.getContent()) {
            dtoList.add(mapToCommentDto(parent));
        }

        return new PageResponse<>(
                dtoList,
                parentPage.getNumber(),
                parentPage.getSize(),
                parentPage.getTotalElements(),
                parentPage.getTotalPages()
        );
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long postId, CommentCreateRequest req, String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자: " + userId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글: " + postId));

        Comment parent = null;
        if (req.getParentId() != null) {
            parent = commentRepository.findById(req.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 댓글: " + req.getParentId()));
        }

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .parent(parent)
                .content(req.getContent())
                .createdAt(now())
                .build();
        Comment saved = commentRepository.save(comment);

        return mapToCommentDto(saved);
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long postId, Long commentId, CommentUpdateRequest req, String userId) {

        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글: " + postId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글: " + commentId));

        if (!comment.getPost().getPostId().equals(postId)) {
            throw new IllegalArgumentException("해당 게시글에 존재하지 않는 댓글입니다.");
        }

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인만 수정할 수 있습니다.");
        }

        comment.updateContent(req.getContent());
        return mapToCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long postId, Long commentId, String userId) {

        postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글: " + postId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글: " + commentId));


        if (!comment.getPost().getPostId().equals(postId)) {
            throw new IllegalArgumentException("해당 게시글에 존재하지 않는 댓글입니다.");
        }

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }

    /*
     * 부모 댓글 엔티티 하나를 받아,
     * 자식 댓글(최신 3개 + 전체 개수)까지 포함한
     * CommentResponseDto로 변환하는 헬퍼 메서드
     */

    private CommentResponseDto mapToCommentDto(Comment parent) {

        List<Comment> childEntities = commentRepository
                .findTop3ByParent_CommentIdOrderByCreatedAtDesc(parent.getCommentId());

        // 전체 자식 댓글 수 집계
        long totalChildCount = commentRepository
                .countByParent_CommentId(parent.getCommentId());

        List<ChildCommentDto> childDtos = new ArrayList<>();
        for (Comment child : childEntities) {
            childDtos.add(ChildCommentDto.fromEntity(child));
        }

        // 부모+자식 합친 응답 DTO 생성
        return CommentResponseDto.fromEntity(parent, childDtos, totalChildCount);
    }
}
    /*
    1.	엔티티 조회(존재 여부)
	2.	권한 검사(애플리케이션 로직)
	3.	도메인 메서드 호출 (updateContent)
	4.	DTO 매핑 및 응답
     */
