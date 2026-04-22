package com.innowise.authservice.exceptions;

public class RefreshTokenInvalidException extends RuntimeException {
  public RefreshTokenInvalidException() {
    super();
  }

  public RefreshTokenInvalidException(String message) {
    super(message);
  }

}
