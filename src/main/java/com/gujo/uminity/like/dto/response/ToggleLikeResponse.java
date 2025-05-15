package com.gujo.uminity.like.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ToggleLikeResponse {
    private boolean likedByMe;
    private long likeCount;
}