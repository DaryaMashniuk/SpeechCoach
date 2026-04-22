package com.innowise.authservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Auth Service API",
                version = "1.0",
                description = "API for securing the app"
        ),
        servers = {
                @Server(
                        url = "http://localhost:8082",
                        description = "Development Server"
                )
        }
)
public class SwaggerConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/swagger-ui/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
                        .resourceChain(false);
        }
}
