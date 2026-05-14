package by.mashnyuk.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

  @Value("${authservice.url}")
  private String authServiceUrl;

  @Value("${orchestratorservice.url}")
  private String orchestratorServiceUrl;

  @Bean
  public RouteLocator customRoutes(RouteLocatorBuilder builder) {
    String authUrl = authServiceUrl.trim();

    return builder.routes()
            .route("auth-public", r -> r
                    .path("/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/auth/refresh","/api/v1/auth/validate")
                    .filters(f -> f.stripPrefix(0))
                    .uri(authUrl))
            .route("orchestrator",r -> r
                    .path("/api/v1/orchestrator/**")
                    .filters(f -> f.stripPrefix(0))
                    .uri(orchestratorServiceUrl))
            .build();
  }
}
