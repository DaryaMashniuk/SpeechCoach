package com.innowise.authservice.exceptions;

import com.innowise.authservice.model.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.security.access.AccessDeniedException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(UserExistsWithUsernameException.class)
  public ResponseEntity<ErrorResponse> handleUserExistsWithUsername(
          UserExistsWithUsernameException ex,
          WebRequest request) {

    logger.warn("Username conflict: {}", ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false),
            null
    );

    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
  }

  @ExceptionHandler(UserServiceException.class)
  public ResponseEntity<ErrorResponse> handleUserServiceException(
          UserServiceException ex,
          WebRequest request) {

    logger.error("User service unavailable", ex);

    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false),
            null
    );

    return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
          MethodArgumentNotValidException exception,
          WebRequest request
  ) {
    Map<String, Object> errors = new HashMap<>();

    exception.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    String errorMessage = "Validation failed for " + errors.size() + " field(s)";
    String requestDescription = request.getDescription(false);
    logger.warn("Validation failed for request {}: {}", requestDescription, errors);
    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            errorMessage,
            request.getDescription(false),
            errors
    );

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
  }

  @ExceptionHandler(RefreshTokenInvalidException.class)
  public ResponseEntity<ErrorResponse> handleRefreshTokenInvalidException(
          RefreshTokenInvalidException exception,
          WebRequest request) {
    logger.error("Refresh token invalid: {}", exception.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
            exception.getMessage(),
            request.getDescription(false),
            null
    );

    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
          UsernameNotFoundException exception,
          WebRequest request) {
    logger.error("Username not found: {}", exception.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
            exception.getMessage(),
            request.getDescription(false),
            null
    );

    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
  }

  @ExceptionHandler(TokenValidationException.class)
  public ResponseEntity<ErrorResponse> handleTokenValidationException(
          TokenValidationException exception,
          WebRequest request) {
    logger.error("Token validation failed {}", exception.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
            exception.getMessage(),
            request.getDescription(false),
            null
    );

    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
            "Invalid username or password",
            request.getDescription(false),
            null
    );
    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false),
            null
    );
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.FORBIDDEN.value(),
            HttpStatus.FORBIDDEN.getReasonPhrase(),
            "You don't have permission to access this resource",
            request.getDescription(false),
            null
    );
    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
  }

  @ExceptionHandler(UserAlreadyExistsWithEmailException.class)
  public ResponseEntity<ErrorResponse> handleUserAlreadyExistsWithEmail(
          UserAlreadyExistsWithEmailException ex,
          WebRequest request) {
    logger.warn("Conflict detected: {}", ex.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false),
            null
    );

    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
          ConstraintViolationException ex,
          WebRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false),
            null
    );

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(
          Exception ex,
          WebRequest request) {

    logger.error("Unexpected error", ex);
    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "Internal server error",
            request.getDescription(false),
            null
    );

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
    String path = ex.getResourcePath();
    if (path != null && (path.contains("swagger") || path.contains("v3/api-docs") || path.contains("webjars"))) {
      return null;
    }

    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            "Resource not found: " + path,
            request.getDescription(false),
            null
    );
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
  }
}
