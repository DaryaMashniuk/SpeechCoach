package com.innowise.authservice.controller.api;

import com.innowise.authservice.model.dto.request.ChangeRoleRequest;
import com.innowise.authservice.model.dto.request.LoginRequestDto;
import com.innowise.authservice.model.dto.request.RefreshTokenRequest;
import com.innowise.authservice.model.dto.request.RegistrationDto;
import com.innowise.authservice.model.dto.request.ValidationRequestDto;
import com.innowise.authservice.model.dto.response.ErrorResponse;
import com.innowise.authservice.model.dto.response.TokenResponseDto;
import com.innowise.authservice.model.dto.response.ValidationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Authentication", description = "API for user authentication, registration, and token management")
@RequestMapping("api/v1/auth")
public interface AuthControllerApi {

  @Operation(
          summary = "User login",
          description = "Authenticates a user and returns access and refresh tokens"
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Authentication successful",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = TokenResponseDto.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "400",
                  description = "Invalid login data provided",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "401",
                  description = "Invalid username or password",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "User not found",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class)
                  )
          )
  })
  @PostMapping("/login")
  ResponseEntity<TokenResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto);

  @Operation(
          summary = "Refresh access token",
          description = "Generates a new access token using a valid refresh token"
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "New tokens generated successfully",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = TokenResponseDto.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "400",
                  description = "Invalid refresh token data",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "401",
                  description = "Invalid or expired refresh token",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class)
                  )
          )
  })
  @PostMapping("/refresh")
  ResponseEntity<TokenResponseDto> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest);

  @Operation(
          summary = "Validate token",
          description = "Validates an access token and returns its claims if valid"
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Token validation successful",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ValidationResponseDto.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "400",
                  description = "Invalid token data provided",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "401",
                  description = "Invalid or expired token",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class)
                  )
          )
  })
  @PostMapping("/validate")
  ResponseEntity<ValidationResponseDto> validate(@RequestBody @Valid ValidationRequestDto validationRequestDto);

  @Operation(
          summary = "Register new user",
          description = "Creates a new user account in the system"
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "User registered successfully",
                  content = @Content(
                          mediaType = "text/plain",
                          schema = @Schema(implementation = String.class, example = "User registered successfully")
                  )
          ),
          @ApiResponse(
                  responseCode = "400",
                  description = "Invalid registration data provided",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "409",
                  description = "User with this email already exists",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class)
                  )
          )
  })
  @PostMapping("/register")
  ResponseEntity<String> register(@RequestBody @Valid RegistrationDto registrationDto);

  @Operation(
          summary = "Change user role",
          description = "Changes the role of a specific user. Only administrators can perform this operation."
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "User role changed successfully"
          ),
          @ApiResponse(
                  responseCode = "400",
                  description = "Invalid role data provided",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "401",
                  description = "User is not authenticated",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "403",
                  description = "User does not have ADMIN role",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class)
                  )
          ),
          @ApiResponse(
                  responseCode = "404",
                  description = "User with specified ID not found",
                  content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class)
                  )
          )
  })
  @PatchMapping("/users/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  ResponseEntity<Void> changeUserRole(@PathVariable Long id, @RequestBody @Valid ChangeRoleRequest changeRoleRequest);
}