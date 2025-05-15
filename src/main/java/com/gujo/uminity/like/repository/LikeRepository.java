package com.gujo.uminity.like.repository;

import com.gujo.uminity.like.entity.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserUserIdAndPostPostId(String userId, Long postId);

    void deleteByUserUserIdAndPostPostId(String userId, Long postId);

    long countByPostPostId(Long postId);

    Page<Like> findByUserUserId(String userId, Pageable pageable);

    @Query("SELECT l.user.name FROM Like l WHERE l.post.postId = :postId")
    Page<String> findLikerNamesByPostId(@Param("postId") Long postId, Pageable pageable);
}

/*
좋아요 관련 데이터 접근 레이어

이유 및 근거:
1. existsByUserUserIdAndPostPostId: 사용자-게시글 좋아요 상태 확인
2. deleteByUserUserIdAndPostPostId: 효율적 토글 삭제
3. countByPostPostId: 실시간 좋아요 개수 동적 집계 지원
4. findByUserUserId: 사용자의 좋아요 목록 페이징 조회 지원
5. findLikerNamesByPostId: 사용자 이름만 String으로 반환하여 DTO 불필요
*/