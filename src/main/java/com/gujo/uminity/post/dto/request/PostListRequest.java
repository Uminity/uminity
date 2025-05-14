package com.gujo.uminity.post.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostListRequest {

    private String keyword = "";

    // 검색 대상 (제목, 내용, 제목+내용)
    private SearchType searchType = SearchType.ALL;

    @Min(value = 0, message = "page는 0이상")
    private int page = 0;

    @Min(value = 0, message = "size는 0이상")
    @Max(value = 30, message = "size는 30이하")
    // ExceptionHandler 에서 전역 예외처리시에 출력될 메시지 추출
    private int size = 10;

    public enum SearchType {
        TITLE, CONTENT, ALL, USERID;
    }
}

/*
생각 정리

컨트롤러에서 다루지말고 그냥 DTO 객체에서 처음부터 검증하고 컨트롤러에서 요청 처리 하는게 나을듯
요청 DTO로 어떤 조건, 어떤 페이지를 달라는 요청을 받는 변환역할

목록 조회 (검색+페이지네이션)
*/