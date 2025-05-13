package com.gujo.uminity.comment.repository;

import com.gujo.uminity.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost_PostIdAndParentIsNullOrderByCreatedAtAsc(Long postId);
}
