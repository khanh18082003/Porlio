package com.porlio.porliobe.module.shared.configuration;

import com.porlio.porliobe.module.admin.service.impl.ApplicationInitService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationInitConfig {

  @Bean
  ApplicationRunner applicationRunner(ApplicationInitService applicationInitService) {
    return args -> applicationInitService.init();
  }
}
