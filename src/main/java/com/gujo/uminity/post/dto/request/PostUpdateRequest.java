package com.gujo.uminity.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostUpdateRequest {
    @NotBlank(message = "제목을 반드시 입력하세요")
    private String title;

    @NotBlank(message = "내용도 입력하세요")
    private String content;
}

/*
생각 정리

update 시 create와 동일하게 동작하는데 그냥 의도를 분명히 하기 위해 따로 요청 DTO 분리

 */

