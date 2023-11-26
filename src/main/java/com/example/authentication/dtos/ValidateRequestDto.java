package com.example.authentication.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateRequestDto {
    private String email;
    private String password;
    private String deviceId;
    private String ipAddress;
}
