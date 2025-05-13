package com.gujo.uminity.common.security;

import com.gujo.uminity.user.entity.Role;
import com.gujo.uminity.user.entity.User;
import com.gujo.uminity.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
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

        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isPresent()) {
            User user = findUser.get();

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
        } else {
            throw new UsernameNotFoundException("이메일을 확인해주세요.");
        }
    }
}
