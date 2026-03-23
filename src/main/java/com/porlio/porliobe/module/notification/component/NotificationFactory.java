package com.porlio.porliobe.module.notification.component;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class NotificationFactory {

  private final TemplateEngine templateEngine;

  public NotificationFactory(final TemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  public String buildVerifyEmailHtml(String username, String verifyUrl) {
    Context context = new Context();
    context.setVariable("username", username);
    context.setVariable("verifyUrl", verifyUrl);

    return templateEngine.process("verification_email.html", context);
  }

  public String buildResetPasswordEmailHtml(String username, String verifyLink) {

    Context context = new Context();
    context.setVariable("username", username);
    context.setVariable("verifyLink", verifyLink);

    return templateEngine.process("reset_password_email.html", context);
  }

  public String buildWelcomeHtml(String username) {
    Context context = new Context();
    context.setVariable("username", username);

    return templateEngine.process("welcome_email.html", context);
  }
}
