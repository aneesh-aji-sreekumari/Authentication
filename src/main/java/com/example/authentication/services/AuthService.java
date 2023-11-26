package com.example.authentication.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.authentication.dtos.UserDto;
import com.example.authentication.enums.SessionStatus;
import com.example.authentication.exceptions.InvalidCredentialException;
import com.example.authentication.exceptions.UserAlreadyExistsException;
import com.example.authentication.exceptions.UserNotFoundException;
import com.example.authentication.models.Session;
import com.example.authentication.models.User;
import com.example.authentication.repositories.RoleRepository;
import com.example.authentication.repositories.SessionRepository;
import com.example.authentication.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    private String secret = "1x.yz.89-qRTw";
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public AuthService(UserRepository userRepository){
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }
    public UserDto signup(String email, String password) throws UserAlreadyExistsException{
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()){
            throw new UserAlreadyExistsException("User Exists");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        User savedUser = userRepository.save(user);
        return UserDto.from(user);
    }
    public ResponseEntity<UserDto> login(String email, String password, String deviceId, String ipAddress) throws UserNotFoundException, InvalidCredentialException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty())
            throw new UserNotFoundException("");
        User user = optionalUser.get();
        if(!bCryptPasswordEncoder.matches(password, user.getPassword()))
            throw new InvalidCredentialException("");
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("email", email);
        headerMap.put("deviceId", deviceId);
        headerMap.put("ipAddress", ipAddress);
        headerMap.put("userId", user.getId());
        headerMap.put("createdAt", Instant.now());
        String jwt = JWT.create()
                .withHeader(headerMap)
                .withClaim("string-claim", "string-value")
                .withClaim("number-claim", 42)
                .withClaim("bool-claim", true)
                .withClaim("datetime-claim", Instant.now())
                .sign(Algorithm.HMAC256(secret));
        Session session = new Session();
        session.setUser(user);
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(jwt);
        session.setExpiringAt(LocalDate.now().plusDays(60));
        Session savedSession = sessionRepository.save(session);
        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add("AUTH_TOKEN", savedSession.getToken());
        return new ResponseEntity<>(UserDto.from(user), headers, HttpStatus.OK);
    }
    public void logout(String email, String password){

    }
    public void validate(String email, String password){

    }
}
