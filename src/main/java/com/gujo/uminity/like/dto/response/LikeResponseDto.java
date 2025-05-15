package com.gujo.uminity.like.dto.response;

import com.gujo.uminity.like.entity.Like;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class LikeResponseDto {
    private Long likeId;
    private String userId;
    private Long postId;
    private LocalDateTime createdAt;

    public static LikeResponseDto fromEntity(Like like) {
        return LikeResponseDto.builder()
                .likeId(like.getLikeId())
                .userId(like.getUser().getUserId())
                .postId(like.getPost().getPostId())
                .createdAt(like.getCreatedAt())
                .build();
    }
}