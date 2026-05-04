package by.mashnyuk.authservice.service.impl;

import by.mashnyuk.authservice.exceptions.RefreshTokenInvalidException;
import by.mashnyuk.authservice.exceptions.UserNotFoundException;
import by.mashnyuk.authservice.model.RefreshToken;
import by.mashnyuk.authservice.model.UserInfo;
import by.mashnyuk.authservice.model.UserInfoDetails;
import by.mashnyuk.authservice.model.dto.request.RefreshTokenRequest;
import by.mashnyuk.authservice.model.dto.response.TokenResponseDto;
import by.mashnyuk.authservice.repository.RefreshTokenRepository;
import by.mashnyuk.authservice.repository.UserInfoRepository;
import by.mashnyuk.authservice.service.RefreshTokenService;
import by.mashnyuk.authservice.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserInfoRepository userInfoRepository;
  private final JwtTokenUtil jwtTokenUtil;
  @Value("${jwt.refreshtoken.expiration}")
  private long refreshTokenExpireTime;

  @Transactional
  @Override
  public RefreshToken createRefreshToken(Long userId) {
    UserInfo userInfo = userInfoRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User not found with id: "+userId));
    RefreshToken refreshToken = refreshTokenRepository.findByUser(userId)
            .orElse(new RefreshToken());

    refreshToken.setUserInfo(userInfo);
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + refreshTokenExpireTime));

    refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }

  @Override
  public TokenResponseDto getNewToken(RefreshTokenRequest refreshTokenRequest) {
    RefreshToken refreshToken = refreshTokenRepository
            .findByToken(refreshTokenRequest.getToken())
            .orElseThrow(() -> new RefreshTokenInvalidException(refreshTokenRequest.getToken()));
    if (validateRefreshToken(refreshToken)) {
      String newToken = jwtTokenUtil.generateToken( new UserInfoDetails(refreshToken.getUserInfo()));
      return TokenResponseDto.builder()
              .refreshToken(refreshToken.getToken())
              .token(newToken)
              .expiresAt(jwtTokenUtil.extractExpiration(newToken))
              .build();
    } else {
      throw new RefreshTokenInvalidException(refreshTokenRequest.getToken());
    }
  }

  @Override
  public boolean validateRefreshToken(RefreshToken refreshToken) {

    if (refreshToken.getExpiryDate().before(new Date(System.currentTimeMillis()))) {
      refreshTokenRepository.delete(refreshToken);
      return false;
    }
    return true;
  }
}
