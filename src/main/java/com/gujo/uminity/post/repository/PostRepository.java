package com.gujo.uminity.post.repository;

import com.gujo.uminity.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    // Pageable 이용
    // Page<Post> findAll(Pageable pageable);
}
