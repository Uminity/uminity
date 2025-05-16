package com.gujo.uminity.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MyAuthenticationSuccessHandler successHandler;
    private final MyAuthenticationFailureHandler failureHandler;
    private final MyUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(daoAuthenticationProvider());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/myPage", "/myPage/**", "/myPage.html", "/api/v1/myPage/**", "/postForm", "/postForm.html")
                .authenticated()
                .anyRequest().permitAll()
        );

        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

        http.formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
        );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        // UsernameNotFoundException 메시지를 숨기지 않도록 설정
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
