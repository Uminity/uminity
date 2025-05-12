package com.gujo.uminity.auth.dto;

import com.gujo.uminity.common.ResultStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegisterResponseDto {

    private ResultStatus result;
}
