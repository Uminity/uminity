package com.gujo.uminity.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

public class CommentUpdateRequest {
    @NotNull(message = "postId null 아님")
    private Long postId;

    private Long parentId;

    @NotBlank(message = "내용 입력 필요")
    private String Content;
}

/*
post와 comment는 N:1이여서 처음에 댓글 등록할 때 postId가 반드시 있어야 한다.
있어야 게시글에 댓글을 달 수 있고 parentId가 있을경우 대댓글로 인식된다.
 */

/*
그런데 중요한 필드가 있다 바로 userId 이다.
왜 userId를 넣지않는가? Dto에

1.	보안: 클라이언트가 userId 를 직접 JSON 바디에 넣으면 다른 사람 ID 로
댓글을 생성하거나 삭제할 수 있는 취약점이 생긴다.

2.	책임 분리:
	DTO는 “어떤 데이터를 받는지”만 정의
	인증 정보는 스프링 시큐리티가 책임지고 주입

	그래서 스프링 시큐리티가 관리하는 현재 로그인된 사용자의 ID를 가져오는거로 한다.
 */