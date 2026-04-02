package com.porlio.porliobe.module.shared.configuration;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SecurityConfig {

  CustomJwtDecoder jwtDecoder;
  CustomAuthenticationEntryPoint authenticationEntryPoint;

  static final String[] PUBLIC_ENDPOINTS = {
      "/api/v1/auth/**",
      "/api/v1/verification/**",
      "/api/v1/sessions/**",
      "/api/v1/users/registration",
  };

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) {
    http
        .authorizeHttpRequests(
            request -> request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS)
                .permitAll()
                .anyRequest()
                .authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder)
                .jwtAuthenticationConverter(converter()))
            .authenticationEntryPoint(authenticationEntryPoint)
        )
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(
            manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .csrf(AbstractHttpConfigurer::disable);

    return http.build();
  }

  @Bean
  public JwtAuthenticationConverter converter() {
    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    grantedAuthoritiesConverter.setAuthorityPrefix("");
    grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");

    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    return converter;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        List.of("*"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
