package com.gujo.uminity.user.dto;

import com.gujo.uminity.user.entity.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {

    private UUID userId;
    private String name;
    private String email;
    private String phone;
    private List<Role> roles = new ArrayList<>();
}
