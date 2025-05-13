package com.gujo.uminity.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostCreateRequest {
    @NotNull
    private String userId;

    @NotBlank(message = "제목을 반드시 입력하세요")
    private String title;

    @NotBlank(message = "내용도 입력하세요")
    private String content;
}

/*
생각 정리

Dto 에는 전달 객체여서 괜찮은데 Entity 에는 @Data 자제
객체 스스로의 책임과 통제가 약화된다
모든 필드에 퍼블릭한 접근 만들고 캡슐화와 무결성 해칠 수 있다.

서버에서 생성되는 필드 postId랑 createdAt은 클라이언트가 보내지말고 서버에서 채워서 응답 DTO에 넣자
책임과 관심사의 분리
게시글 생성/ 수정 입력


userId를 추가해야 누가쓴 글인지 알 수 있다.
 */

