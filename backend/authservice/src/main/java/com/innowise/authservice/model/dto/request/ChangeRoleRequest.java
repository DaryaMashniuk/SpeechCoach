package com.innowise.authservice.model.dto.request;

import com.innowise.authservice.model.Roles;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Dto to change user role(ADMIN only)")
public class ChangeRoleRequest {
  @NotNull(message = "Role is required")
  @Schema(
          description = "User role",
          example = "USER",
          allowableValues = {"USER", "ADMIN"}
  )
  @Enumerated(EnumType.STRING)
  private Roles role;
}
