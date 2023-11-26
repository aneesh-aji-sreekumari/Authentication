package com.example.authentication.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    private String email;
    private String password;
    private String deviceId;
    private String ipAddress;
}
