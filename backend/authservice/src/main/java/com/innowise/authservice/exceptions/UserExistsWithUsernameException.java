package com.innowise.authservice.exceptions;

public class UserExistsWithUsernameException extends RuntimeException{

  public UserExistsWithUsernameException(String username) {
    super("Username " + username + " already exists");
  }

  public UserExistsWithUsernameException(String username, Throwable cause) {
    super("Username " + username + " already exists", cause);
  }
}
