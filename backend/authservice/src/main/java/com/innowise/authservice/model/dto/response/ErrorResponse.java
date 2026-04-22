package com.innowise.authservice.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for API error response")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse implements Serializable {
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
  private Instant timestamp;
  @Schema(description = "HTTP status code", example = "400")
  private int status;
  @Schema(description = "HTTP status reason phrase", example = "Bad Request")
  private String error;
  @Schema(description = "Error message", example = "Validation failed for 2 field(s)")
  private String message;
  @Schema(description = "Request path", example = "/api/v1/auth")
  private String path;
  @Schema(description = "Additional error details (e.g., validation errors)")
  private Map<String, Object> details;

  public ErrorResponse(Instant timestamp, int status, String error,
                       String message, String path) {
    this(timestamp, status, error, message, path, null);
  }
}

