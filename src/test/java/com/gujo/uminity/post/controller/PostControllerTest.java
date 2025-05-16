package com.gujo.uminity.post.controller;

import com.gujo.uminity.post.service.PostService;
import com.gujo.uminity.resolver.ViewCookieArgumentResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private MockMvc mockMvc;
    private final Long POST_ID = 42L;

    @BeforeEach
    void 인터셉터설정() {
        HandlerMethodArgumentResolver viewCookieResolver = new ViewCookieArgumentResolver();
        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .setCustomArgumentResolvers(viewCookieResolver)
                .build();
    }

    @Test
    @DisplayName("처음조회에는 쿠키 없어서 incrementViewCount 호출 쿠키 추가")
    void 처음조회() throws Exception {
        long postId = 1L;

        // 1) getPost 은 null 반환해도 OK (body 검증은 하지 않음)
        when(postService.getPost(postId)).thenReturn(null);
        doNothing().when(postService).incrementViewCountIfNew(postId, true);

        mockMvc.perform(get("/api/posts/{postId}", postId))
                .andExpect(status().isOk())
                // 쿠키가 설정됐는지 확인 (헤더 존재 + 값 검증)
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().string("Set-Cookie",
                        org.hamcrest.Matchers.containsString("VIEWED_POSTS=" + postId)));

        verify(postService).incrementViewCountIfNew(postId, true);
    }

    @Test
    @DisplayName("이미 본 게시글이면 incrementViewCount 미호출 및 새로운 쿠키 미추가")
    void 이미본게시글() throws Exception {
        long postId = 1L;

        // 이미 본 상태를 흉내내기 위해 쿠키를 URL-encoded 값으로 생성
        String encoded = URLEncoder.encode(String.valueOf(postId), StandardCharsets.UTF_8);
        MockCookie viewedCookie = new MockCookie("VIEWED_POSTS", encoded);

        when(postService.getPost(postId)).thenReturn(null);
        doNothing().when(postService).incrementViewCountIfNew(postId, false);

        mockMvc.perform(get("/api/posts/{postId}", postId)
                        .cookie(viewedCookie))
                .andExpect(status().isOk())
                // Set-Cookie 헤더가 **없어야** 합니다
                .andExpect(header().doesNotExist("Set-Cookie"));

        verify(postService).incrementViewCountIfNew(postId, false);
    }
}
