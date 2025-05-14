package com.gujo.uminity.common;

import lombok.Builder;

@Builder
public record ErrorResponse(
        int status,
        String code,
        String message,
        String detail
) {
}