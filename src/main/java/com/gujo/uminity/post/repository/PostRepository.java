package com.gujo.uminity.post.repository;

import com.gujo.uminity.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    // Pageable 이용
    // Page<Post> findAll(Pageable pageable);

    // 1. 제목만 검색
    Page<Post> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // 2. 내용만 검색
    Page<Post> findByContentContainingIgnoreCase(String content, Pageable pageable);

    // 3. 제목 or 내용 검색?
    Page<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase
    (String title, String content, Pageable pageable);

    // 이러면 제목만, 제목 + 내용 할 수 있겠네
    // 이러면 %keyword% 적용
    // title 또는 content에 keyword 포함된 항목 조회하려고
}

/*
 생각 정리
 DB랑 접근하고 있는 레포지토리는 원하는 것만 빠르게 꺼낼 수 있다.
 클라이언트로 데이터 보내고 브라우저에서 필터링하기엔 네트워크 비용이 클 것이다.

 */