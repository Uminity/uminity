package com.gujo.uminity.auth.dto;

import com.gujo.uminity.common.response.ResultStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RegisterResponseDto {
    ResultStatus result;
}
