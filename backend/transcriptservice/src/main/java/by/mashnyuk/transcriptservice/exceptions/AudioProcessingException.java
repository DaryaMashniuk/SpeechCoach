package by.mashnyuk.transcriptservice.exceptions;

public class AudioProcessingException extends RuntimeException {
  public AudioProcessingException(String message) {
    super(message);
  }
  public AudioProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
  public AudioProcessingException(Throwable cause) {
    super(cause);
  }
  public AudioProcessingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
