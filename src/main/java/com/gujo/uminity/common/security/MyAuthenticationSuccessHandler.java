package com.gujo.uminity.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gujo.uminity.auth.dto.LoginResponseDto;
import com.gujo.uminity.user.entity.Role;
import com.gujo.uminity.user.repository.RoleRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        MyUserDetails principal = (MyUserDetails) authentication.getPrincipal();

        List<Role> roles = principal.getAuthorities().stream()
                .map(ga -> ga.getAuthority().replace("ROLE_", ""))
                .map(roleRepository::findRoleByRoleName)
                .toList();

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .userId(principal.getUserId())
                .email(principal.getUsername())
                .name(principal.getName())
                .phone(principal.getPhone())
                .roles(roles)
                .build();

        objectMapper.writeValue(response.getWriter(), loginResponseDto);

        log.info("로그인 성공");
    }
}
