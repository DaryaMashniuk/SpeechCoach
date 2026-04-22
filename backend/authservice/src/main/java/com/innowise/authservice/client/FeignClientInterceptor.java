package com.innowise.authservice.client;

import com.innowise.authservice.model.Roles;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

  @Override
  public void apply(RequestTemplate requestTemplate) {
    requestTemplate
            .header("X-User-Id", String.valueOf(0))
            .header("X-User-Role", String.valueOf(Roles.ADMIN));
  }
}
