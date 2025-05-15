package com.gujo.uminity.auth.controller;

import com.gujo.uminity.auth.dto.RegisterRequestDto;
import com.gujo.uminity.auth.dto.RegisterResponseDto;
import com.gujo.uminity.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@Validated @RequestBody RegisterRequestDto registerRequestDto) {
        return ResponseEntity.ok(authService.register(registerRequestDto));
    }
}
