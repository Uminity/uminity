package com.gujo.uminity.common.response;

import lombok.Builder;

@Builder
public record ErrorResponse(
        int status,
        String code,
        String message,
        String detail
) {
}