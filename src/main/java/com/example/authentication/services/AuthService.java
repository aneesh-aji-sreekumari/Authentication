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
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.RequestHeader;

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
        String jwt = jwtTokenCreator(deviceId, ipAddress, user.getId().toString(), secret);
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
    public ResponseEntity<String> logout(String authToken, Long userId){
        Optional<Session> optionalSession = sessionRepository.findSessionByToken(authToken);
        if(optionalSession.isEmpty())
            return new ResponseEntity<>("No session exists", HttpStatus.NOT_FOUND);
        Session session = optionalSession.get();
        if(session.getSessionStatus().equals(SessionStatus.LOGGED_OUT))
            return new ResponseEntity<>("U r already Loggedout", HttpStatus.NOT_ACCEPTABLE);
        session.setSessionStatus(SessionStatus.LOGGED_OUT);
        sessionRepository.save(session);
        return new ResponseEntity<>("U have loggedout Successfully", HttpStatus.OK);
    }
    public ResponseEntity<SessionStatus> validate(String deviceId, String ipAddress, String authToken, String userId){
        if(jwtTokenCreator(deviceId, ipAddress, userId, secret).equals(authToken)) {
            Optional<Session> optionalSession = sessionRepository.findSessionByToken(authToken);
            Session session = optionalSession.get();
            if (session.getSessionStatus().equals(SessionStatus.ACTIVE))
                return new ResponseEntity<>(SessionStatus.ACTIVE, HttpStatus.ACCEPTED);
            else
                return new ResponseEntity<>(SessionStatus.EXPIRED, HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(SessionStatus.INVALID, HttpStatus.NOT_FOUND);
    }
    private String jwtTokenCreator(String deviceId, String ipAddress, String userId, String secret){
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("deviceId", deviceId);
        headerMap.put("ipAddress", ipAddress);
        headerMap.put("userId", userId);
        String jwt = JWT.create()
                .withHeader(headerMap)
                .sign(Algorithm.HMAC256(secret));
        return jwt;
    }
}
