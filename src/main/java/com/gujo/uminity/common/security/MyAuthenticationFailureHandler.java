package com.gujo.uminity.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gujo.uminity.common.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        System.out.println(exception.getMessage());
        if (exception.getMessage().equals("탈퇴된 회원입니다.")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            ErrorResponse errorResponse = ErrorResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .code("AUTHENTICATION_FAILED")
                    .message("탈퇴된 회원입니다.")
                    .detail(exception.getMessage())
                    .build();

            objectMapper.writeValue(response.getWriter(), errorResponse);
            System.out.println("제발 되라");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            ErrorResponse errorResponse = ErrorResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .code("AUTHENTICATION_FAILED")
                    .message("로그인에 실패했습니다.")
                    .detail(exception.getMessage())
                    .build();
            System.out.println("=====================================================");

            objectMapper.writeValue(response.getWriter(), errorResponse);
        }
    }
}
