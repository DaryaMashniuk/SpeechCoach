package by.mashnyuk.orchestratorservice.exceptions;

public class AudioDeleteException extends RuntimeException {
  public AudioDeleteException(String message) {
    super(message);
  }
  public AudioDeleteException(String message, Throwable cause) {
    super(message, cause);
  }
  public AudioDeleteException(Throwable cause) {
    super(cause);
  }
  public AudioDeleteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
