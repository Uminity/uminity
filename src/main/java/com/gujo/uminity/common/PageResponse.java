package com.gujo.uminity.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;

    // 걍 DB 전체 게시글 수
    private long totalElements;
    // 전체 구성되어야 되는 페이지 개수 == 전체 게시글 수 / size 한거의 올림 버전
    private long totalPages;
}

/*
제네릭 타입을 쓴 이유가 페이징할 대상 DTO 타입을 나중에 지정하기 위해
페이지만 할게 아니라 댓글이랑 좋아요 누른 사람들도 해보려고
 */