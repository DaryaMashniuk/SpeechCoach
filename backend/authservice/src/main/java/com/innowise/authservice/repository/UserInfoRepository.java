package com.innowise.authservice.repository;

import com.innowise.authservice.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo,Long> {
  Optional<UserInfo> findByEmail(String email);

  Optional<UserInfo> findByUsername(String username);
}
