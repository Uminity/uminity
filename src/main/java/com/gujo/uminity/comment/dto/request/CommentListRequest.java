package com.gujo.uminity.comment.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentListRequest {
    @Min(value = 0, message = "page 는 0 이상")
    private int page = 0;

    @Min(value = 1, message = "size 는 1이상")
    private int size = 5;
}
