package com.gujo.uminity.post.dto.response;

import com.gujo.uminity.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder

public class PostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Integer viewCnt;

    public static PostResponseDto fromEntity(Post p) {
        return PostResponseDto.builder()
                .postId(p.getPostId())
                .title(p.getTitle())
                .content(p.getContent())
                .createdAt(p.getCreatedAt())
                .viewCnt(p.getViewCnt())
                .build();
    }
}

/*
서버 생성값 Id, 시간, 조회수
클라이언트 입력 값 제목, 내용
컨트롤러가 해당 DTO를 JSON 으로 응답
 */