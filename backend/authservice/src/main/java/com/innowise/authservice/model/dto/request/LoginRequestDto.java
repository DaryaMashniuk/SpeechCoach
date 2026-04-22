package com.innowise.authservice.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO for user login request")
public class LoginRequestDto {

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
  @Schema(
          description = "User's username",
          example = "john_doe",
          minLength = 3,
          maxLength = 100
  )
  private String username;

  @NotBlank(message = "Password is required")
  @Size(min = 5, max = 100, message = "Password must be between 6 and 100 characters")
  @Schema(
          description = "User's password",
          example = "securePassword123",
          minLength = 6,
          maxLength = 100
  )
  private String password;
}