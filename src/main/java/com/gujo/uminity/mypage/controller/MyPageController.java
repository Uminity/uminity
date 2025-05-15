package com.gujo.uminity.mypage.controller;

import com.gujo.uminity.comment.service.CommentService;
import com.gujo.uminity.common.PageResponse;
import com.gujo.uminity.common.security.MyUserDetails;
import com.gujo.uminity.mypage.dto.MyCommentRequestDto;
import com.gujo.uminity.mypage.dto.MyCommentResponseDto;
import com.gujo.uminity.mypage.dto.MyPageResponseDto;
import com.gujo.uminity.mypage.dto.UpdateUserInfoRequestDto;
import com.gujo.uminity.mypage.service.MyPageService;
import com.gujo.uminity.post.dto.request.PostListRequest;
import com.gujo.uminity.post.dto.response.PostResponseDto;
import com.gujo.uminity.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/myPage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final PostService postService;
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<MyPageResponseDto> getMyPageInfo() {
        return ResponseEntity.ok(myPageService.getMyPageInfo());
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMyInfo(@RequestBody UpdateUserInfoRequestDto updateUserInfoRequestDto) {
        myPageService.updateUserInfo(updateUserInfoRequestDto);
    }

    @GetMapping("/posts")
    public ResponseEntity<PageResponse<PostResponseDto>> listPosts(@Validated @ModelAttribute PostListRequest postListRequest) {
        PageResponse<PostResponseDto> page = postService.listPosts(postListRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/comments")
    public ResponseEntity<PageResponse<MyCommentResponseDto>> listMyComments(@Validated @ModelAttribute MyCommentRequestDto myCommentRequestDto) {
        PageResponse<MyCommentResponseDto> page = commentService.listMyComments(myCommentRequestDto);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        String userId = myUserDetails.getUserId();
        System.out.println(userId);
        myPageService.deleteUser(myUserDetails.getUserId());
    }
}
