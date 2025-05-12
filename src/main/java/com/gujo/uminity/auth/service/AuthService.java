package com.gujo.uminity.auth.service;

import com.gujo.uminity.auth.dto.RegisterRequestDto;
import com.gujo.uminity.auth.dto.RegisterResponseDto;

public interface AuthService {

    RegisterResponseDto register(RegisterRequestDto registerRequestDto);
}
