package com.gujo.uminity.comment.repository;

import com.gujo.uminity.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPost_PostIdAndParentIsNull(Long postId, Pageable pageable);

    List<Comment> findTop3ByParent_CommentIdOrderByCreatedAtDesc(Long parentId);

    long countByParent_CommentId(Long parentId);
}


/*
메서드 설명
1. REST 요청 시 쿼리파라미터로 최상위 댓글만 페이징 조회
2. 최상위 댓글의 자식 댓글 조회 일단 3개까지만 조회한다 생각하고
3. 자식댓글의 개수 구하는거
 */