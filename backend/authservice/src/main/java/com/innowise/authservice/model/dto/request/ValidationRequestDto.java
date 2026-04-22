package com.innowise.authservice.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO for token validation request")
public class ValidationRequestDto {

  @NotBlank(message = "Token is required")
  @Schema(
          description = "JWT token to validate",
          example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  )
  private String token;
}