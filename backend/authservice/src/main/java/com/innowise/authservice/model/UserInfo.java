package com.innowise.authservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_info")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, length = 100, unique = true)
  private String username;
  @Column(nullable = false, length = 100)
  private String password;
  @Column(nullable = false, unique = true)
  private String email;

  @Enumerated(EnumType.STRING)
  private Roles role;

}
