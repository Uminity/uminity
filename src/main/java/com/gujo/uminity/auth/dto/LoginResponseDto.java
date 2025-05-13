package com.gujo.uminity.auth.dto;

import com.gujo.uminity.user.entity.Role;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResponseDto {

    private String userId;
    private String email;
    private String name;
    private String phone;
    private List<Role> roles = new ArrayList<>();
}
