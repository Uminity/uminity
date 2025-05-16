package com.gujo.uminity.post.controller;

import com.gujo.uminity.post.dto.response.PostResponseDto;
import com.gujo.uminity.post.service.PostService;
import com.gujo.uminity.resolver.ViewCookieArgumentResolver;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        // given
        PostResponseDto dto = PostResponseDto.builder()
                .postId(POST_ID)
                .authorName("Author")
                .title("Title")
                .content("Content")
                .createdAt(null)
                .viewCnt(1)
                .build();
        given(postService.getPost(POST_ID)).willReturn(dto);

        mockMvc.perform(get("/api/posts/{postId}", POST_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(POST_ID))
                .andExpect(cookie().exists("VIEWED_POSTS"));

        then(postService).should().incrementViewCountIfNew(POST_ID, true);
    }

    @Test
    @DisplayName("이미 본 게시글이면 incrementViewCount 미호출 및 새로운 쿠키 미추가")
    void 이미본게시글() throws Exception {
        PostResponseDto dto = PostResponseDto.builder()
                .postId(POST_ID)
                .authorName("Author")
                .title("Title")
                .content("Content")
                .createdAt(null)
                .viewCnt(1)
                .build();
        given(postService.getPost(POST_ID)).willReturn(dto);

        mockMvc.perform(get("/api/posts/{postId}", POST_ID)
                        .cookie(new Cookie("VIEWED_POSTS", String.valueOf(POST_ID)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(POST_ID))
                .andExpect(cookie().doesNotExist("VIEWED_POSTS"));

        then(postService).should(never()).incrementViewCountIfNew(POST_ID, true);
    }
}
