package com.hangoutz.app.repository;

import com.hangoutz.app.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, String> {

    Optional<Token> findByToken(String token);
}
