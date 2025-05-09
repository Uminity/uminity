package com.gujo.uminity.auth.dto;

import com.gujo.uminity.user.entity.Role;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {

    private String name;
    private String email;
    private String password;
    private String phone;
    private List<Role> roles = new ArrayList<>();
}
