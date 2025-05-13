package com.gujo.uminity.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           MyAuthenticationSuccessHandler successHandler, MyAuthenticationFailureHandler failureHandler) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()
                .requestMatchers("/login.html").hasRole("USER")
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated());

        http.csrf(AbstractHttpConfigurer::disable);

        http.formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .permitAll()
        );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
