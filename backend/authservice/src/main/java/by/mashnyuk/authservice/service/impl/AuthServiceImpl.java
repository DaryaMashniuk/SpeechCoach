package by.mashnyuk.authservice.service.impl;

import by.mashnyuk.authservice.exceptions.TokenValidationException;
import by.mashnyuk.authservice.exceptions.UserAlreadyExistsWithEmailException;
import by.mashnyuk.authservice.exceptions.UserExistsWithUsernameException;
import by.mashnyuk.authservice.exceptions.UserNotFoundException;
import by.mashnyuk.authservice.model.RefreshToken;
import by.mashnyuk.authservice.model.Roles;
import by.mashnyuk.authservice.model.UserInfo;
import by.mashnyuk.authservice.model.UserInfoDetails;
import by.mashnyuk.authservice.model.dto.request.ChangeRoleRequest;
import by.mashnyuk.authservice.model.dto.request.LoginRequestDto;
import by.mashnyuk.authservice.model.dto.request.RegistrationDto;
import by.mashnyuk.authservice.model.dto.response.TokenResponseDto;
import by.mashnyuk.authservice.model.dto.response.ValidationResponseDto;
import by.mashnyuk.authservice.model.dto.request.ValidationRequestDto;
import by.mashnyuk.authservice.repository.UserInfoRepository;
import by.mashnyuk.authservice.service.AuthService;
import by.mashnyuk.authservice.service.RefreshTokenService;
import by.mashnyuk.authservice.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final AuthenticationManager authenticationManager;
  private final RefreshTokenService refreshTokenService;
  private final UserInfoRepository userInfoRepository;
  private final JwtTokenUtil jwtTokenUtil;

  @Override
  public TokenResponseDto authenticate(LoginRequestDto loginRequestDto) {
    Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(),loginRequestDto.getPassword())
    );
    UserInfoDetails userInfoDetails = (UserInfoDetails) auth.getPrincipal();
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDetails.getId());
    String token = jwtTokenUtil.generateToken(userInfoDetails);
    return TokenResponseDto.builder()
            .token(token)
            .expiresAt(jwtTokenUtil.extractExpiration(token))
            .refreshToken(refreshToken.getToken())
            .build();
  }

  @Transactional
  @Override
  public void save(RegistrationDto registrationDto) {
    if (userInfoRepository.findByUsername(registrationDto.getUsername()).isPresent()) {
      throw new UserExistsWithUsernameException(registrationDto.getUsername());
    }
    String email = registrationDto.getEmail();
    if (userInfoRepository.findByEmail(email).isPresent()){
      throw new UserAlreadyExistsWithEmailException("User already exists with email "+email);
    }

    UserInfo userInfo = new UserInfo();
    userInfo.setUsername(registrationDto.getUsername());
    userInfo.setPassword(bCryptPasswordEncoder.encode(registrationDto.getPassword()));
    userInfo.setEmail(email);
    userInfo.setRole(Roles.USER);

    UserInfo savedUser = userInfoRepository.save(userInfo);

  }

  @Override
  public ValidationResponseDto validate(ValidationRequestDto validationRequestDto){
    try{
      String token = validationRequestDto.getToken();

      Long userId = jwtTokenUtil.extractId(token);
      UserInfo userInfo = userInfoRepository.findById(userId)
              .orElseThrow(() -> new TokenValidationException("User not found"));

      UserInfoDetails userInfoDetails = new UserInfoDetails(userInfo);

      return ValidationResponseDto.builder()
              .valid(jwtTokenUtil.validateToken(token, userInfoDetails))
              .expiresAt(jwtTokenUtil.extractExpiration(token))
              .role(jwtTokenUtil.extractRole(token))
              .userId(jwtTokenUtil.extractId(token))
              .build();
    } catch (ExpiredJwtException e) {
      throw new TokenValidationException("Token expired");
    }
    catch (JwtException e) {
      throw new TokenValidationException("Invalid token");
    }

  }

  @Transactional
  @Override
  public void changeRole(Long id,ChangeRoleRequest changeRoleRequest) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserInfoDetails current = (UserInfoDetails) auth.getPrincipal();

    if (current.getId().equals(id)) {
      throw new AccessDeniedException("Cannot change your own role");
    }
    UserInfo userInfo = findById(id);
    userInfo.setRole(changeRoleRequest.getRole());
  }

  @Override
  public UserInfo findById(long id) {
    return userInfoRepository.findById(id).orElseThrow(()-> new UserNotFoundException("User not found with id : "+id));
  }
}

