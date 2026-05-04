package by.mashnyuk.transcriptservice.exceptions;

public class TranscriptionException extends RuntimeException {
  public TranscriptionException(String message) {
    super(message);
  }
  public TranscriptionException(String message, Throwable cause) {
    super(message, cause);
  }
  public TranscriptionException(Throwable cause) {
    super(cause);
  }
  public TranscriptionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
