package by.mashnyuk.authservice.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
@Schema(description = "DTO for user registration request")
public class RegistrationDto {

  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and underscores")
  @Schema(
          description = "Unique username",
          example = "john_doe",
          minLength = 3,
          maxLength = 50,
          pattern = "^[a-zA-Z0-9_]+$"
  )
  private String username;

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
  @Pattern(
          regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
          message = "Password must contain at least one digit, one lowercase, one uppercase, one special character and no whitespace"
  )
  @Schema(
          description = "User password (must meet complexity requirements)",
          example = "Secure@Pass123",
          minLength = 8,
          maxLength = 100
  )
  private String password;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  @Schema(
          description = "User email address",
          example = "john.doe@example.com",
          maxLength = 100,
          format = "email"
  )
  private String email;

}