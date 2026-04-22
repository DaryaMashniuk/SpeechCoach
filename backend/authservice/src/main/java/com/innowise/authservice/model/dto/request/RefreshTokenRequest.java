package com.innowise.authservice.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO for refresh token request")
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    @Size(min = 16, message = "Token must be at least 32 characters")
    @Schema(
            description = "Refresh token string",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            minLength = 16
    )
    String token;
}