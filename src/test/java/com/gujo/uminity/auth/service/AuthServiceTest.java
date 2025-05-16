package com.gujo.uminity.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gujo.uminity.auth.dto.RegisterRequestDto;
import com.gujo.uminity.user.repository.RoleRepository;
import com.gujo.uminity.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private RoleRepository roleRepository;

    @Test
    void 회원가입_성공() {
        RegisterRequestDto request = new RegisterRequestDto("테스트", "test1@test.com", "1!", "010-1111-1111");

        when(userRepository.existsByEmail("test1@test.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("1!");

        authService.register(request);

        verify(userRepository).save(any());
    }

    @Test
    void 회원가입_실패() {
        RegisterRequestDto request = new RegisterRequestDto("테스트", "test1@test.com", "1!", "010-1111-1111");

        when(userRepository.existsByEmail("test1@test.com")).thenReturn(true);
        Assertions.assertThrows(IllegalArgumentException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any());
    }
}
