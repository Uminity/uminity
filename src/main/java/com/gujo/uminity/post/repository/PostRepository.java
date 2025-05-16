package com.gujo.uminity.post.repository;

import com.gujo.uminity.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // 4. user_userId 기반 검색
    Page<Post> findByUser_UserIdOrderByCreatedAtDesc(String userId, Pageable pageable);


    @Modifying
    @Query("update Post p set p.viewCnt = p.viewCnt + 1 where p.postId = :postId")
    int incrementViewCount(@Param("postId") Long postId);
    // findById로 SELECT 한다음 post.setViewCnt 로 업데이트할 때 또 update 쿼리를 가져오니까
    // postId는 무조건 있다고 생각해서 그냥 조회없이 바로 업데이트하게끔?
}

/*
 생각 정리
 DB랑 접근하고 있는 레포지토리는 원하는 것만 빠르게 꺼낼 수 있다.
 클라이언트로 데이터 보내고 브라우저에서 필터링하기엔 네트워크 비용이 클 것이다.

 */