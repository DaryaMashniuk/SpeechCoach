package com.innowise.authservice.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class UserInfoDetails implements UserDetails {

  private Long id;
  private String username;
  private String password;
  private GrantedAuthority authorities;

  public UserInfoDetails(UserInfo userInfo) {
    this.id = userInfo.getId();
    this.username = userInfo.getUsername();
    this.password = userInfo.getPassword();
    this.authorities = new SimpleGrantedAuthority("ROLE_" + userInfo.getRole().toString());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(authorities);
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
