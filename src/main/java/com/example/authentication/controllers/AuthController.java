package com.example.authentication.controllers;

import com.example.authentication.dtos.*;
import com.example.authentication.exceptions.InvalidCredentialException;
import com.example.authentication.exceptions.UserAlreadyExistsException;
import com.example.authentication.exceptions.UserNotFoundException;
import com.example.authentication.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;
    public AuthController(AuthService authService){
        this.authService = authService;
    }
    //login Api
    @GetMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) throws UserNotFoundException, InvalidCredentialException {
        ResponseEntity<UserDto> userDtoResponseEntity = authService.login(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword(),
                loginRequestDto.getDeviceId(),
                loginRequestDto.getIpAddress());
        return userDtoResponseEntity;
    }
    @GetMapping("/logout")
    public String logout(@RequestHeader LogoutRequestDto logoutRequestDto){
        return "U have hit the logout API";
    }
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto requestDto) throws UserAlreadyExistsException {
        UserDto userDto = authService.signup(requestDto.getEmail(), requestDto.getPassword());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    @GetMapping("/validate")
    public String validate(@RequestHeader ValidateRequestDto validateRequestDto){
        return "U have hit the validate API";
    }

}
