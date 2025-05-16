package com.gujo.uminity.mypage.controller;

import com.gujo.uminity.comment.service.CommentService;
import com.gujo.uminity.common.security.MyUserDetails;
import com.gujo.uminity.common.web.PageResponse;
import com.gujo.uminity.like.service.LikeService;
import com.gujo.uminity.mypage.dto.MyPageResponseDto;
import com.gujo.uminity.mypage.dto.PasswordChangeRequestDto;
import com.gujo.uminity.mypage.dto.UpdateUserInfoRequestDto;
import com.gujo.uminity.mypage.service.MyPageService;
import com.gujo.uminity.post.dto.request.PostListRequest;
import com.gujo.uminity.post.dto.response.PostResponseDto;
import com.gujo.uminity.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/myPage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final PostService postService;

    private final LikeService likeService;
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


    @GetMapping("/likes")
    public ResponseEntity<PageResponse<PostResponseDto>> getMyLikedPosts(
            @AuthenticationPrincipal MyUserDetails principal,
            Pageable pageable) {
        String userId = principal.getUserId();
        PageResponse<PostResponseDto> response = likeService.getMyLikedPosts(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@AuthenticationPrincipal MyUserDetails myUserDetails) {
        myPageService.deleteUser(myUserDetails.getUserId());
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@RequestBody PasswordChangeRequestDto passwordChangeRequestDto,
                               @AuthenticationPrincipal MyUserDetails myUserDetails) {
        String userId = myUserDetails.getUserId();
        myPageService.changePassword(userId, passwordChangeRequestDto);
    }
}
