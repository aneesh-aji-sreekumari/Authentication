package com.example.authentication.repositories;

import com.example.authentication.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    public Optional<Session> findSessionByToken(String token);

}
