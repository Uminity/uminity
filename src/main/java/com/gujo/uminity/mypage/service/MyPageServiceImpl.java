package com.gujo.uminity.mypage.service;

import com.gujo.uminity.mypage.dto.MyPageResponseDto;
import com.gujo.uminity.user.entity.Role;
import com.gujo.uminity.user.entity.User;
import com.gujo.uminity.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public MyPageResponseDto getMyPageInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저가 없습니다."));

        List<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .toList();

        return MyPageResponseDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(roleNames)
                .build();
    }
}
