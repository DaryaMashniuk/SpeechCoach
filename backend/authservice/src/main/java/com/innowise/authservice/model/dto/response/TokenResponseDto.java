package com.innowise.authservice.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
@Schema(description = "DTO for authentication token response")
public class TokenResponseDto {

  @Schema(
          description = "JWT access token",
          example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  )
  private String token;

  @Schema(
          description = "Refresh token for obtaining new access tokens",
          example = "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4..."
  )
  private String refreshToken;

  @Schema(
          description = "Access token expiration date/time",
          example = "2024-01-19T10:30:00.000Z"
  )
  private Date expiresAt;
}