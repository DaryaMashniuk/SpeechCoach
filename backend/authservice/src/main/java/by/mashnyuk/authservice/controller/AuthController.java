package by.mashnyuk.authservice.controller;

import by.mashnyuk.authservice.controller.api.AuthControllerApi;
import by.mashnyuk.authservice.model.dto.request.ChangeRoleRequest;
import by.mashnyuk.authservice.model.dto.request.LoginRequestDto;
import by.mashnyuk.authservice.model.dto.request.RefreshTokenRequest;
import by.mashnyuk.authservice.model.dto.request.RegistrationDto;
import by.mashnyuk.authservice.model.dto.response.TokenResponseDto;
import by.mashnyuk.authservice.model.dto.request.ValidationRequestDto;
import by.mashnyuk.authservice.model.dto.response.ValidationResponseDto;
import by.mashnyuk.authservice.service.RefreshTokenService;
import by.mashnyuk.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/auth")
public class AuthController implements AuthControllerApi {

  private final AuthService authService;
  private final RefreshTokenService refreshTokenService;

  @Override
  @PostMapping("/login")
  public ResponseEntity<TokenResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
    return ResponseEntity.ok().body(authService.authenticate(loginRequestDto));
  }

  @Override
  @PostMapping("/refresh")
  public ResponseEntity<TokenResponseDto> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
     return ResponseEntity.ok().body(refreshTokenService.getNewToken(refreshTokenRequest));
  }

  @Override
  @PostMapping("/validate")
  public ResponseEntity<ValidationResponseDto> validate(@RequestBody @Valid ValidationRequestDto validationRequestDto) {
    return ResponseEntity.ok().body(authService.validate(validationRequestDto));
  }

  @Override
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody @Valid RegistrationDto registrationDto) {
    authService.save(registrationDto);
    return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
  }

  @Override
  @PatchMapping("/users/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> changeUserRole(@PathVariable Long id, @RequestBody @Valid ChangeRoleRequest changeRoleRequest) {
    authService.changeRole(id,changeRoleRequest);
    return ResponseEntity.ok().build();
  }
}
