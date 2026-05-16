package by.mashnyuk.orchestratorservice.exceptions;

public class ResultDoesNotExistException extends RuntimeException {
  public ResultDoesNotExistException() {
    super();
  }
  public ResultDoesNotExistException(String message) {
    super(message);
  }
  public ResultDoesNotExistException(String message, Throwable cause) {
    super(message, cause);
  }
  public ResultDoesNotExistException(Throwable cause) {
    super(cause);
  }
}
