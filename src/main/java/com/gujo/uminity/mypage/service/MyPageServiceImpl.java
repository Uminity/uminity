package com.gujo.uminity.mypage.service;

import com.gujo.uminity.mypage.dto.MyPageResponseDto;
import com.gujo.uminity.mypage.dto.UpdateUserInfoRequestDto;
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

        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new IllegalArgumentException("유저가 없습니다."));

        List<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .toList();

        return MyPageResponseDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(roleNames)
                .build();
    }

    @Override
    @Transactional
    public void updateUserInfo(UpdateUserInfoRequestDto updateUserInfoRequestDto) {

        // 1) 현재 로그인된 사용자 ID 꺼내기 (Spring Security)
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        // 2) 엔티티 조회
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new IllegalArgumentException("유저가 없습니다."));

        user.setName(updateUserInfoRequestDto.getName());
        user.setPhone(updateUserInfoRequestDto.getPhone());
    }

    @Transactional
    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자:" + userId));

        user.setDeleted(true);
    }
}
