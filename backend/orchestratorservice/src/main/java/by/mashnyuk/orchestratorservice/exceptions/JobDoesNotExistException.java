package by.mashnyuk.orchestratorservice.exceptions;

public class JobDoesNotExistException extends RuntimeException {
  public JobDoesNotExistException(String message) {
    super(message);
  }
  public JobDoesNotExistException(String message, Throwable cause) {
    super(message, cause);
  }
  public JobDoesNotExistException(Throwable cause) {
    super(cause);
  }
  public JobDoesNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
