package com.gujo.uminity.common.security;

import com.gujo.uminity.user.entity.Role;
import com.gujo.uminity.user.entity.User;
import com.gujo.uminity.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("아이디 또는 비밀번호가 잘못되었습니다."));

        if (user.isDeleted()) {
            throw new UsernameNotFoundException("탈퇴된 회원입니다.");
        }

        List<Role> roles = user.getRoles();
        List<SimpleGrantedAuthority> authorities =
                roles.stream().map(Role::getRoleName).map(name -> "ROLE_" + name).map(SimpleGrantedAuthority::new).toList();

        return MyUserDetails.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .phone(user.getPhone())
                .authorities(authorities)
                .build();

    }
}
