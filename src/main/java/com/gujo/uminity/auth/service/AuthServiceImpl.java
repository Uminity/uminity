package com.gujo.uminity.auth.service;

import com.gujo.uminity.auth.dto.RegisterRequestDto;
import com.gujo.uminity.auth.dto.RegisterResponseDto;
import com.gujo.uminity.common.ResultStatus;
import com.gujo.uminity.user.entity.Role;
import com.gujo.uminity.user.entity.User;
import com.gujo.uminity.user.repository.RoleRepository;
import com.gujo.uminity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final String USER = "USER";

    @Override
    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {

        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = new User();
        Role findRole = roleRepository.findRoleByRoleName(USER);

        user.setName(registerRequestDto.getName());
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setPhone(registerRequestDto.getPhone());
        user.getRoles().add(findRole);
        userRepository.save(user);

        return RegisterResponseDto.builder()
                .result(ResultStatus.SUCCESS)
                .build();
    }
}
