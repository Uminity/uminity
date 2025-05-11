package com.gujo.uminity.post.dto;

import com.gujo.uminity.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private Long postId;
    private String title;
    private LocalDateTime createdAt;
    private Integer viewCnt;

    public static PostDto fromEntity(Post post) {
        return PostDto.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .createdAt(post.getCreatedAt())
                .viewCnt(post.getViewCnt())
                .build();
    }
}
/*
Dto 에는 전달 객체여서 괜찮은데 Entity 에는 @Data 자제
객체 스스로의 책임과 통제가 약화된다
모든 필드에 퍼블릭한 접근 만들고 캡슐화와 무결성 해칠 수 있다.
 */