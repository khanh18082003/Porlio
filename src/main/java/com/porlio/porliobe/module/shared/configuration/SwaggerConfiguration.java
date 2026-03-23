package com.porlio.porliobe.module.shared.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

// Cấu hình Swagger/OpenAPI cho môi trường phát triển và kiểm thử
@Configuration
@Profile({"dev", "test"})
public class SwaggerConfiguration {

  // Cấu hình để bỏ qua bảo mật cho các endpoint của Swagger UI và OpenAPI docs
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring()
        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs*/**");
  }

  // Cấu hình nhóm API cho Swagger/OpenAPI, quét các controller trong package chỉ định
  @Bean
  public GroupedOpenApi publicApi(@Value("${openapi.service.api-docs}") String apiDocs) {
    return GroupedOpenApi.builder()
        .group(apiDocs)
        .packagesToScan("com.porlio.porliobe.module")
        .pathsToMatch("/api/v1/**")
        .build();
  }

  // Cấu hình OpenAPI với thông tin về API, server, và bảo mật Metadata và cấu hình bảo mật
  @Bean
  public OpenAPI openAPI(
      @Value("${openapi.service.title}") String title,
      @Value("${openapi.service.version}") String version,
      @Value("${openapi.service.server}") String serverUrl
  ) {
    return new OpenAPI()
        .servers(List.of(new Server().url(serverUrl)))
        .addSecurityItem(new SecurityRequirement()
            .addList("Bearer Authentication"))
        .components(new Components()
            .addSecuritySchemes("Bearer Authentication", createBearerAuthScheme()))
        .info(new Info()
            .title(title)
            .description("API documentation for Porlio platform")
            .version(version)
            .license(new License().name("Apache 2.0").url("https://springdoc.org")));
  }

  private SecurityScheme createBearerAuthScheme() {
    return new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .description("Enter JWT token");
  }
}
