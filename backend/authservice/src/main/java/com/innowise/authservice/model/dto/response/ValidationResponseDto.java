package com.innowise.authservice.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
@Schema(description = "DTO for token validation response")
public class ValidationResponseDto {

  @Schema(
          description = "Indicates whether the token is valid",
          example = "true"
  )
  private boolean valid;

  @Schema(
          description = "User role from token claims (if valid)",
          example = "USER",
          allowableValues = {"USER", "ADMIN"}
  )
  private String role;

  @Schema(
          description = "User ID from token claims (if valid)",
          example = "123"
  )
  private Long userId;

  @Schema(
          description = "Token expiration date/time",
          example = "2024-01-19T10:30:00.000Z"
  )
  private Date expiresAt;
}