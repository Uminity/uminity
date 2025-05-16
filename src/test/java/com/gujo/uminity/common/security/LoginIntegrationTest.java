package com.gujo.uminity.common.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

import com.gujo.uminity.user.entity.User;
import com.gujo.uminity.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        // 테스트용 계정 미리 생성
        User user = User.builder()
                .email("test1@test.com")
                .password(passwordEncoder.encode("1!"))
                .name("테스트유저")
                .phone("010-1111-1111")
                .build();
        userRepository.save(user);
    }

    @Test
    void 로그인_성공() throws Exception {
        mockMvc.perform(formLogin()
                        .loginProcessingUrl("/login")
                        .user("email", "test1@test.com")
                        .password("password", "1!"))
                .andExpect(authenticated().withUsername("test1@test.com"));
    }

    @Test
    void 로그인_실패__잘못된_비밀번호() throws Exception {
        mockMvc.perform(formLogin()
                        .loginProcessingUrl("/login")
                        .user("email", "test1@test.com")
                        .password("password", "wrong"))
                .andExpect(unauthenticated());
    }

    @Test
    void 로그인_실패__존재하지_않는_사용자() throws Exception {
        mockMvc.perform(formLogin()
                        .loginProcessingUrl("/login")
                        .user("email", "nouser@test.com")
                        .password("password", "whatever"))
                .andExpect(unauthenticated());
    }
}
