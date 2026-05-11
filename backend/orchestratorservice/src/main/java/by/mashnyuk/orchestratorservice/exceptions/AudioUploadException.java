package by.mashnyuk.orchestratorservice.exceptions;

public class AudioUploadException extends RuntimeException {
  public AudioUploadException(String message) {
    super(message);
  }
  public AudioUploadException(String message, Throwable cause) {
    super(message, cause);
  }
  public AudioUploadException(Throwable cause) {
    super(cause);
  }
  public AudioUploadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
