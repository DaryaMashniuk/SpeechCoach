package com.innowise.authservice.service.impl;

import com.innowise.authservice.model.UserInfo;
import com.innowise.authservice.model.UserInfoDetails;
import com.innowise.authservice.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {


  private final UserInfoRepository userInfoRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserInfo userInfo = userInfoRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    return new UserInfoDetails(userInfo);
  }
}
