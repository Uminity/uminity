package com.gujo.uminity.mypage.controller;

import com.gujo.uminity.mypage.dto.MyPageResponseDto;
import com.gujo.uminity.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/myPage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping
    public ResponseEntity<MyPageResponseDto> getMyPageInfo() {
        return ResponseEntity.ok(myPageService.getMyPageInfo());
    }
}
