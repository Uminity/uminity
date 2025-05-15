package com.gujo.uminity.post.controller;

import com.gujo.uminity.config.interceptor.ViewCountInterceptor;
import com.gujo.uminity.post.dto.response.PostResponseDto;
import com.gujo.uminity.post.service.PostService;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PostControllerWithInterceptorTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private ViewCountInterceptor viewCountInterceptor;
    private MockMvc mockMvc;

    private final Long POST_ID = 42L;

    @BeforeEach
    void 인터셉터설정() {
        viewCountInterceptor = new ViewCountInterceptor(postService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(postController)
                .addInterceptors(viewCountInterceptor)
                .build();
    }

    @Test
    @DisplayName("처음조회에는 쿠키 없어서 incrementViewCount 호출하고 쿠키 추가")
    void 처음조회() throws Exception {
        // given
        PostResponseDto dto = PostResponseDto.builder()
                .postId(POST_ID)
                .authorName("사람")
                .title("제목")
                .content("내용")
                .createdAt(null)
                .viewCnt(1)
                .build();
        given(postService.getPost(POST_ID)).willReturn(dto);

        // when / then
        mockMvc.perform(get("/api/posts/{postId}", POST_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(POST_ID))
                // 인터셉터가 Set-Cookie 헤더에 쿠키를 추가하는지 확인
                .andExpect(cookie().exists("VIEWED_POST_" + POST_ID))
                .andExpect(cookie().value("VIEWED_POST_" + POST_ID, "true"));

        then(postService).should().incrementViewCount(POST_ID);
    }

    @Test
    @DisplayName("이미 본 게시글이면 incrementViewCount 미호출 및 새로운 쿠키 미추가")
    void 이미본게시글() throws Exception {
        // given
        Cookie existing = new Cookie("VIEWED_POST_" + POST_ID, "true");
        PostResponseDto dto = PostResponseDto.builder()
                .postId(POST_ID)
                .authorName("사람")
                .title("제목")
                .content("내용")
                .createdAt(null)
                .viewCnt(1)
                .build();
        given(postService.getPost(POST_ID)).willReturn(dto);

        // when / then
        mockMvc.perform(get("/api/posts/{postId}", POST_ID)
                        .cookie(existing)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(POST_ID))
                // 인터셉터가 새로운 쿠키를 추가하지 않아야 함
                .andExpect(cookie().doesNotExist("VIEWED_POST_" + POST_ID));

        then(postService).should(never()).incrementViewCount(POST_ID);
    }
}
