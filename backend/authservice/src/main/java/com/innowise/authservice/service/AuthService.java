package com.innowise.authservice.service;

import com.innowise.authservice.model.UserInfo;
import com.innowise.authservice.model.dto.request.ChangeRoleRequest;
import com.innowise.authservice.model.dto.request.LoginRequestDto;
import com.innowise.authservice.model.dto.request.RegistrationDto;
import com.innowise.authservice.model.dto.response.TokenResponseDto;
import com.innowise.authservice.model.dto.request.ValidationRequestDto;
import com.innowise.authservice.model.dto.response.ValidationResponseDto;

/**
 * Service interface for authentication and user management operations.
 * Provides methods for user authentication, registration, token validation,
 * and user information retrieval.
 */
public interface AuthService {

  /**
   * Changes the role of a specific user in the system.
   * Allows administrators to modify user roles (e.g., from USER to ADMIN or vice versa).
   * Validates that the target user exists and that the requested role change is valid.
   *
   * @param id the unique identifier of the user whose role should be changed
   * @param changeRoleRequest the request containing the new role information
   * @throws com.innowise.authservice.exceptions.UserNotFoundException if no user is found with the specified ID
   */
  void changeRole(Long id, ChangeRoleRequest changeRoleRequest);

  /**
   * Finds a user by their unique identifier.
   *
   * @param id the unique identifier of the user to find
   * @return the {@link UserInfo} object containing user details
   * @throws com.innowise.authservice.exceptions.UserNotFoundException if no user is found with the specified ID
   */
  UserInfo findById(long id);

  /**
   * Registers a new user in the system.
   * Validates user data, checks for existing users with the same username or email,
   * and saves the new user to the database with encrypted password.
   *
   * @param registrationDto the registration data transfer object containing user details
   * @throws com.innowise.authservice.exceptions.UserExistsWithUsernameException if a user with the same username already exists
   * @throws com.innowise.authservice.exceptions.UserAlreadyExistsWithEmailException if a user with the same email already exists
   */
  void save(RegistrationDto registrationDto);

  /**
   * Authenticates a user with provided credentials and returns authentication tokens.
   * Performs username/password validation and generates JWT access token and refresh token.
   *
   * @param loginRequestDto the login data transfer object containing username and password
   * @return {@link TokenResponseDto} containing access token, refresh token, and expiration information
   * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if user with the specified username doesn't exist
   */
  TokenResponseDto authenticate(LoginRequestDto loginRequestDto);

  /**
   * Validates a JWT token and extracts user information from it.
   * Checks token expiration and signature validity, then extracts claims
   * such as user ID, role, and expiration time.
   *
   * @param validationRequestDto the validation request containing the token to validate
   * @return {@link ValidationResponseDto} containing validation result and token claims
   * @throws com.innowise.authservice.exceptions.TokenValidationException if the token is invalid, expired, or malformed
   */
  ValidationResponseDto validate(ValidationRequestDto validationRequestDto);
}