package com.innowise.authservice.service;

import com.innowise.authservice.model.RefreshToken;
import com.innowise.authservice.model.dto.request.RefreshTokenRequest;
import com.innowise.authservice.model.dto.response.TokenResponseDto;

/**
 * Service interface for refresh token management operations.
 * Handles creation, validation, and rotation of refresh tokens used
 * for obtaining new access tokens without requiring user re-authentication.
 */
public interface RefreshTokenService {

  /**
   * Creates a new refresh token for a user.
   * If the user already has an existing refresh token, it is deleted
   * before creating a new one to ensure single active refresh token per user.
   *
   * @param userId the unique identifier of the user for whom to create the refresh token
   * @return the created {@link RefreshToken} entity
   * @throws com.innowise.authservice.exceptions.UserNotFoundException if no user is found with the specified ID
   */
  RefreshToken createRefreshToken(Long userId);

  /**
   * Validates a refresh token by checking its expiration date.
   * If the token is expired, it is automatically deleted from the database.
   *
   * @param refreshToken the refresh token entity to validate
   * @return {@code true} if the token is valid and not expired, {@code false} otherwise
   */
  boolean validateRefreshToken(RefreshToken refreshToken);

  /**
   * Generates a new access token using a valid refresh token.
   * Validates the refresh token, then creates a new JWT access token
   * for the associated user while preserving the same refresh token.
   *
   * @param refreshTokenRequest the request containing the refresh token string
   * @return {@link TokenResponseDto} containing the new access token and preserved refresh token
   * @throws com.innowise.authservice.exceptions.RefreshTokenInvalidException if the refresh token is invalid, expired, or not found
   */
  TokenResponseDto getNewToken(RefreshTokenRequest refreshTokenRequest);
}