package com.gujo.uminity.mypage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gujo.uminity.comment.entity.Comment;
import com.gujo.uminity.comment.repository.CommentRepository;
import com.gujo.uminity.post.entity.Post;
import com.gujo.uminity.post.repository.PostRepository;
import com.gujo.uminity.user.entity.User;
import com.gujo.uminity.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MyPageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockHttpSession session;
    private String userId;

    @BeforeEach
    void setUp() throws Exception {
        // 사용자, 게시글, 댓글 초기화
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트용 사용자 생성
        User user = User.builder()
                .email("test1@test.com")
                .password(passwordEncoder.encode("1!"))
                .name("테스트유저")
                .phone("010-1111-1111")
                .build();
        userRepository.save(user);
        userId = user.getUserId();
        // 테스트용 게시글 생성
        Post post = postRepository.save(
                Post.of(user, "제목", "내용")
        );
        // 3) 댓글 생성 (팩토리 메서드 of 이용)
        Comment comment = commentRepository.save(
                Comment.of(post, user, "댓글", null)
        );

        // 로그인 후 세션 저장
        MvcResult result = mockMvc.perform(formLogin()
                        .loginProcessingUrl("/login")
                        .user("email", "test1@test.com")
                        .password("password", "1!"))
                .andExpect(authenticated().withUsername("test1@test.com"))
                .andReturn();
        session = (MockHttpSession) result.getRequest().getSession(false);
    }

    @Test
    void 내_정보_조회() throws Exception {
        mockMvc.perform(get("/api/v1/myPage").session(session).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test1@test.com"))
                .andExpect(jsonPath("$.name").value("테스트유저"))
                .andExpect(jsonPath("$.phone").value("010-1111-1111"));
    }

    @Test
    void 내_정보_수정() throws Exception {
        mockMvc.perform(patch("/api/v1/myPage").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"변경이름\",\"phone\":\"010-0000-0000\"}"))
                .andExpect(status().isNoContent());

        User updated = userRepository.findByEmail("test1@test.com").get();
        assertEquals("변경이름", updated.getName());
        assertEquals("010-0000-0000", updated.getPhone());
    }

    @Test
    void 비밀번호_변경() throws Exception {
        mockMvc.perform(patch("/api/v1/myPage/password").session(session).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"1!\",\"newPassword\":\"2@\"}"))
                .andExpect(status().isOk());

        User updated = userRepository.findByEmail("test1@test.com").get();
        assertTrue(passwordEncoder.matches("2@", updated.getPassword()));
    }

    @Test
    void 내_게시글_목록_조회() throws Exception {
        mockMvc.perform(get("/api/v1/myPage/posts")
                        .param("page", "0")
                        .param("size", "10")
                        .param("searchType", "USERID")
                        .param("keyword", userId)
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("제목"));
    }

    @Test
    void 내_댓글_목록_조회() throws Exception {
        mockMvc.perform(get("/api/v1/myPage/comments")
                        .param("page", "0")
                        .param("size", "10")
                        .param("userId", userId)
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("댓글"));
    }

    @Test
    void 회원_탈퇴() throws Exception {
        mockMvc.perform(delete("/api/v1/myPage").session(session).with(csrf()))
                .andExpect(status().isOk());

        User deletedUser = userRepository.findByEmail("test1@test.com").orElseThrow();
        assertTrue(deletedUser.isDeleted());
        assertNull(deletedUser.getPhone());
    }


}