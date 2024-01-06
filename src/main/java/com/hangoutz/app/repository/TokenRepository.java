package com.hangoutz.app.repository;

import com.hangoutz.app.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, String> {

    Optional<Token> findByToken(String token);

    @Modifying
    @Query(value = """
            DELETE FROM Token t WHERE t.user.id=:userId
            """)
    void deleteAllTokensOfUser(String userId);
}
