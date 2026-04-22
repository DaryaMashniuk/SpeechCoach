package com.innowise.authservice.config;


import com.innowise.authservice.repository.UserInfoRepository;
import com.innowise.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.innowise.authservice.model.dto.request.RegistrationDto;
import com.innowise.authservice.model.UserInfo;
import com.innowise.authservice.model.Roles;

import java.time.LocalDate;


@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

  private final UserInfoRepository repository;
  private final AuthService authService;
  private static final Logger logger = LogManager.getLogger(AdminInitializer.class);

  @Override
  @Transactional
  public void run(String... args) {
    if (repository.findByUsername("admin").isEmpty()) {
      try {
        RegistrationDto adminDto = RegistrationDto.builder()
                .username("admin")
                .password("Secure@Pass123")
                .email("admin@innowise.com")
                .name("Admin")
                .surname("System")
                .birthDate(LocalDate.of(1990, 1, 1))
                .active(true)
                .build();

        authService.save(adminDto);

        UserInfo adminUser = repository.findByUsername("admin")
                .orElseThrow(() -> new RuntimeException("Admin not found after save"));

        adminUser.setRole(Roles.ADMIN);
        repository.save(adminUser);

        logger.info("Admin user initialized successfully");
      } catch (Exception e) {
        logger.warn("Failed to initialize admin: " + e.getMessage());
      }
    }
  }
}