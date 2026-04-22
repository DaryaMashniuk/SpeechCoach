package com.innowise.authservice.config;

import com.innowise.authservice.util.JwtTokenUtil;
import com.innowise.authservice.service.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtTokenUtil jwtTokenUtil;
  private final UserDetailsServiceImpl userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      String header = request.getHeader("Authorization");
      String token = null;
      String username = null;
      if (header != null && header.startsWith("Bearer ")) {
        token = header.substring(7);
        username = jwtTokenUtil.extractUsername(token);
      }
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);
          if (jwtTokenUtil.validateToken(token, userDetails)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
          }
      }

      filterChain.doFilter(request, response);
    } catch (ExpiredJwtException e) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token expired");
    } catch (JwtException | IllegalArgumentException e) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
    }
  }
}
