package com.gujo.uminity.mypage.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyPageResponseDto {

    private String name;
    private String email;
    private String phone;
    private List<String> roles = new ArrayList<>();
}
