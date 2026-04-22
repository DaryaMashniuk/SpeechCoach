package com.innowise.authservice.repository;

import com.innowise.authservice.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);

  @Query("SELECT rt FROM RefreshToken rt WHERE rt.userInfo.id = :id")
  Optional<RefreshToken> findByUser(@Param("id") Long id);
}
