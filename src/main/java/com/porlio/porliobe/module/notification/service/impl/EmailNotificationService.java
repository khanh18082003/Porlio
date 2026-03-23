package com.porlio.porliobe.module.notification.service.impl;

import com.porlio.porliobe.module.notification.component.NotificationFactory;
import com.porlio.porliobe.module.notification.dto.EmailMessage;
import com.porlio.porliobe.module.notification.service.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "EMAIL_NOTIFICATION_SERVICE")
public class EmailNotificationService implements NotificationService {

  JavaMailSender mailSender;
  NotificationFactory notificationFactory;

  @NonFinal
  @Value("${app.mail.from}")
  String fromEmail;

  @NonFinal
  @Value("${app.base-url}")
  String baseUrl;

  @Override
  @Async("notificationTaskExecutor")
  public void sendVerificationEmail(String toEmail, String username, String verifyToken) {
    String verifyLink = baseUrl + "/verify-email?token=" + verifyToken;

    EmailMessage message = EmailMessage.builder()
        .to(toEmail)
        .subject("Verify your email — Porlio Team")
        .htmlContent(notificationFactory.buildVerifyEmailHtml(username, verifyLink))
        .type("VERIFY_EMAIL")
        .build();

    send(message);
  }

  @Override
  @Async("notificationTaskExecutor")
  public void sendPasswordResetEmail(String toEmail, String username, String resetToken) {
    String verifyLink = baseUrl + "/reset-password?token=" + resetToken;
    EmailMessage message = EmailMessage.builder()
        .to(toEmail)
        .subject("Reset Your Password — Porlio Team")
        .htmlContent(notificationFactory.buildResetPasswordEmailHtml(username, verifyLink))
        .type("RESET_PASSWORD")
        .build();
    send(message);
  }

  @Override
  public void sendWelcomeEmail(String toEmail, String username) {
    EmailMessage message = EmailMessage.builder()
        .to(toEmail)
        .subject("Chào mừng đến với Portfolio Builder!")
        .htmlContent(notificationFactory.buildWelcomeHtml(username))
        .type("WELCOME")
        .build();

    send(message);
  }

  private void send(EmailMessage emailMessage) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(
          mimeMessage, true, "UTF-8"
          // true = multipart (support HTML + attachment)
          // UTF-8 = hỗ trợ tiếng Việt
      );

      helper.setFrom(fromEmail, "Porlio Team");
      helper.setTo(emailMessage.getTo());
      helper.setSubject(emailMessage.getSubject());
      helper.setText(emailMessage.getHtmlContent(), true);

      mailSender.send(mimeMessage);

      log.info("Email sent successfully | type={} to={}",
          emailMessage.getType(), emailMessage.getTo());

    } catch (MessagingException e) {
      // Không throw exception ra ngoài vì đây là async
      // Throw ra ngoài sẽ không có ai catch được
      log.error("Failed to send email | type={} to={} error={}",
          emailMessage.getType(),
          emailMessage.getTo(),
          e.getMessage());

      // TODO v2: Lưu vào bảng failed_emails để retry sau
    } catch (UnsupportedEncodingException e) {
      log.error("Invalid email encoding | type={} to={} error={}",
          emailMessage.getType(),
          emailMessage.getTo(),
          e.getMessage());
    }
  }
}
