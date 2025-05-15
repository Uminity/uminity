package com.gujo.uminity.mypage.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MyCommentRequestDto {

    @NotBlank
    @NotNull
    private String userId;

    @Min(value = 0, message = "page 는 0 이상")
    private int page = 0;

    @Min(value = 1, message = "size 는 1이상")
    private int size = 5;
}
