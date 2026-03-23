package com.porlio.porliobe.module.shared.configuration;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import lombok.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class LocaleResolver extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {

  @NonNull
  @Override
  public Locale resolveLocale(HttpServletRequest request) {
    String languageHeader = request.getHeader("Accept-Language");

    if (StringUtils.hasLength(languageHeader)) {
      List<Locale> supportedLocales = List.of(
          Locale.ENGLISH,
          Locale.of("vi")
      );
      Locale locale = Locale.lookup(Locale.LanguageRange.parse(languageHeader), supportedLocales);
      return (locale != null) ? locale : Locale.ENGLISH; // Default if unsupported
    }

    return Locale.ENGLISH;
  }

  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("classpath:messages");
    messageSource.setDefaultEncoding("UTF-8");
    messageSource.setCacheSeconds(3600);
    return messageSource;
  }
}
